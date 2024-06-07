/*
	채팅 통합 처리를 위한 SharedWorker
	로그 확인 -> chrome://inspect/#workers
*/

//커넥션 객체
let WEBSOCKET = null;
let EVENTSOURCE = null;
const PORT_LIST = [];

//포트 리스트 생성자
function PortListElementBuilder(port) {
	this.port = port;
	this.chatRoomIdx = null;
}

//최초 SharedWorker 접속시 SSE 연결
if(!EVENTSOURCE && !WEBSOCKET)
	connectSSE();

//SharedWorker 연결
self.onconnect = function(e) {
	const port = e.ports[0];

	PORT_LIST.push(new PortListElementBuilder(port));

	//함수로 분리
	port.onmessage = function(e) {
		console.log("SharedWorker, onmessage, e.data.action=" + e.data.action);
		switch(e.data.action) {
			//탭 닫기
			case "closePort":
				let idx;
				for(let i = 0; i < PORT_LIST.length; i++) {
					if(PORT_LIST[i].port == port) {
						idx = i;
						break;
					}
				}
				PORT_LIST.splice(idx, 1);
				console.log("SharedWorker, port removed");
				if(PORT_LIST.length < 1) {
					console.log("SharedWorker, closed");
					//서버와의 연결 끊기
					disconnectSSE();
					disconnectWebSocket();
					self.close();
				}
				break;
			case "chatRoomEnter":
				chatRoomEnter(e.data, port)
				.then((result) => {
					if(result) {
						for(let key in PORT_LIST) {
							if(PORT_LIST[key].port == port) {
								PORT_LIST[key].chatRoomIdx = e.data.payload.roomidx;
							}
						}
					} else {
						//실패시 웹소켓을 끊는다
						disconnectWebSocket();
						connectSSE();
					}
				});
				break;
			case "chatRoomLeave":
				chatRoomLeave(e.data, port)
				.then((result) => {
					if(result) {
						let crlFlag = true;
						for(let key in PORT_LIST) {
							if(PORT_LIST[key].port == port) {
								PORT_LIST[key].chatRoomIdx = null;
							} else if(!!PORT_LIST[key].chatRoomIdx) {
								//채팅방 접속중인 다른 탭이 있는지 확인
								crlFlag = false;
							}
						}
						//접속중인 채팅방이 없으면 연결을 내린다
						if(crlFlag) {
							console.log("why");
							disconnectWebSocket();
							connectSSE();
						}
					}
				});
				break;
			case "loadChat":
				loadChat(e.data, port);
				break;
			case "sendChat":
				sendChat(e.data, port);
				break;
		}
	};
};

//broadcast
function broadcastMsg(by, data) {
	console.log("broadcast, SharedWorker, by=" + by);
	console.log(data);
	PORT_LIST.forEach((val) => {
		val.port.postMessage({
			action: data.act,
			payload: data.payload
		});
	});
}

//chatRoomEnter 처리
async function chatRoomEnter(data, port) {
	//웹소켓 연결이 없는 경우에만
	if(!WEBSOCKET) {
		disconnectSSE();
		try {
			await connectWebSocket();
		} catch(e) {
			port.postMessage({
				action: "chatRoomEnter",
				isSuccess: false
			});
			return false;
		}
	}

	WEBSOCKET.send(JSON.stringify({
		act: "chatRoomEnter",
		payload: {
			roomidx: data.payload.roomidx
		}
	}));
	try {
		const res = await promised_WebSocketReceiver("chatRoomEnter");
		if(!!res.payload.reason) {
			port.postMessage({
				action: "chatRoomEnter",
				isSuccess: false
			});
			return false;
		}
		port.postMessage({
			action: "chatRoomEnter",
			isSuccess: true,
			payload: res.payload
		});
		return true;
	} catch(e) {
		port.postMessage({
			action: "chatRoomEnter",
			isSuccess: false
		});
		return false;
	}
}

//chatRoomLeave 처리
async function chatRoomLeave(data, port) {
	WEBSOCKET.send(JSON.stringify({
		act: "chatRoomLeave",
		payload: {
			roomidx: data.payload.roomidx
		}
	}));
	try {
		const res = await promised_WebSocketReceiver("chatRoomLeave");
		if(!!res.payload.reason) {
			port.postMessage({
				action: "chatRoomLeave",
				isSuccess: false
			});
			return false;
		}
		port.postMessage({
			action: "chatRoomLeave",
			isSuccess: true,
			payload: res.payload
		});
		return true;
	} catch(e) {
		port.postMessage({
			action: "chatRoomLeave",
			isSuccess: false
		});
		return false;
	}
}

//loadChat 처리
async function loadChat(data, port) {
	WEBSOCKET.send(JSON.stringify({
		act: "loadChat",
		payload: {
			roomidx: data.payload.roomidx,
			startChatDetailIdx: data.payload.startChatDetailIdx
		}
	}));
	try {
		const res = await promised_WebSocketReceiver("loadChat");
		if(!!res.payload.reason) {
			port.postMessage({
				action: "loadChat",
				isSuccess: false
			});
			return;
		}
		port.postMessage({
			action: "loadChat",
			isSuccess: true,
			payload: res.payload
		});
		return;
	} catch(e) {
		port.postMessage({
			action: "loadChat",
			isSuccess: false
		});
		return;
	}
}

//sendChat 처리
async function sendChat(data, port) {
	WEBSOCKET.send(JSON.stringify({
		act: "sendChat",
		payload: {
			roomidx: data.payload.roomidx,
			chatContent: data.payload.chatContent
		}
	}));

	try {
		const res = await promised_WebSocketReceiver("sendChat");
		console.log(res);
		if(!!res.payload.reason) {
			port.postMessage({
				action: "sendChat",
				isSuccess: false
			})
			return;
		}
		port.postMessage({
			action: "sendChat",
			isSuccess: true,
			payload: res.payload
		});
		return;
	} catch(e) {
		port.postMessage({
			action: "sendChat",
			isSuccess: false
		})
		return;
	}
}

//1회성 수신 함수
async function promised_WebSocketReceiver(act, millisec = 3000) {
	return new Promise((resolve, reject) => {
        const receiver = (e) => {
            const data = JSON.parse(e.data);
            if (data.act === act) {
				console.log("promised_WebSocketReceiver resolved");
				console.log(data);
                WEBSOCKET.removeEventListener("message", receiver);
                clearTimeout(timeoutId); // 타임아웃 중단
                resolve(data);
            }
        };

        const timeoutId = setTimeout(() => {
            console.log("promised_WebSocketReceiver timeout call");
			if(!!WEBSOCKET)
            	WEBSOCKET.removeEventListener("message", receiver);
            reject(new Error("timeout"));
        }, millisec);

        WEBSOCKET.addEventListener("message", receiver);
    });
}
//연결 종료
function disconnectSSE() {
	if(!!EVENTSOURCE) {
		EVENTSOURCE.close();
		EVENTSOURCE = null;
	}
}
function disconnectWebSocket() {
	if(!!WEBSOCKET) {
		WEBSOCKET.close(1000);
		WEBSOCKET = null;
	}
}
//SSE 연결
function connectSSE() {
	EVENTSOURCE = new EventSource("http://localhost:8080/sse/subscribe");
	
	EVENTSOURCE.addEventListener("open", (e) => {
		console.log("SSE open");
	});
	EVENTSOURCE.addEventListener("connected", (e) => {
		console.log("SSE received msg=connected");
	});
	EVENTSOURCE.addEventListener("broadcastChat", (e) => {
		console.log("SSE received msg=broadcast");
		const data = JSON.parse(e.data);
		if(data.act === "broadcastChat")
			broadcastMsg("SSE", data);
	});
	EVENTSOURCE.onerror = (err) => {
		console.log("SSE onerror=" + err);
		// EVENTSOURCE.close();
		// 이새끼가 있으면 자동 재연결이 안되는거같음
	};
	EVENTSOURCE.onclose = () => {
		console.log("SSE closed");
	};
}
//웹소켓 연결
async function connectWebSocket() {
	console.log("connWS");

	WEBSOCKET = new WebSocket("ws://localhost:8080/wschat");
	
	WEBSOCKET.onopen = (e) => {
		console.log("WebSocket open");
	};
	WEBSOCKET.onmessage = (e) => {
		console.log("WebSocket received msg");
		console.log(e.data);
		console.log(JSON.parse(e.data));
		const data = JSON.parse(e.data);
		if(data.act === "broadcastChat")
			broadcastMsg("WebSocket", data);
	};
	WEBSOCKET.onerror = (e) => {
		WEBSOCKET.close();
		console.log("WebSocket onerror=" + e);
	};
	WEBSOCKET.onclose = (e) => {
		console.log("WebSocket closed");
	};

	return new Promise((resolve, reject) => {
		const dcs = (e) => {
			WEBSOCKET.removeEventListener("open", dcs);
			clearTimeout(timeoutId);
			resolve();
		};
		const timeoutId = setTimeout(() => {
			if(!!WEBSOCKET)
				WEBSOCKET.removeEventListener("open", dcs);
			reject(new Error("timeout"));
		}, 3000);
		WEBSOCKET.addEventListener("open", dcs);
	});
}