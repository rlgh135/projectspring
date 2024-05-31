/*
	채팅 통합 처리를 위한 SharedWorker
	여기 로그들은
	chrome://inspect/#workers
	로 가야만 볼 수 있음
*/

/*
	메모 240528
	웹소켓 열고 닫는거 관련
	여기다가 채팅창이 열리고 닫힌 브라우저 컨텍스트가 몇개 있는지를
	기록하는 변수를 만들어야 함
	지금 상태로는 채팅창 하나가 닫히면 다른 채팅창이 열린 브라우저 컨텍스트가 몇개
	있던간에 웹소켓이 닫힘
	이걸 기록하고 컨트롤할 변수와 로직이 필요함
*/

/*
	프로토콜? 240529
	브라우저 컨텍스트 -> 공유 워커
	{
		action: "...",
		...
	}
	공유 워커 -> 브라우저 컨텍스트
	{
		actRes: "...",
		...
	}
*/

//웹소켓 커넥션
let WEBSOCKET = null;
//SSE 커넥션
let EVENTSOURCE = null;

//Web Worker port
const PORT_LIST = [];

//미확인 메시지 개수
let uncheckedMsgCnt = 0;

//SharedWorker 연결
self.onconnect = function(e) {
	const port = e.ports[0];
	
	PORT_LIST.push(port);
	
	port.onmessage = function(e) {
		console.log("웹워커 onmessage e.data.action=" + e.data.action);
		
		switch(e.data.action) {
			//closePort는 addEventListener - beforeunload 시 호출하게 해야 함
			//https://stackoverflow.com/questions/13662089/javascript-how-to-know-if-a-connection-with-a-shared-worker-is-still-alive
            case "closePort":
                let idx;
                for(let i = 0; i < PORT_LIST.length; i++) {
                    if(PORT_LIST[i] == port) {
                        idx = i;
                    }
                }
                PORT_LIST.splice(idx, 1);
				console.log("공유 웹워커 포트 1 개 삭제");
                if(PORT_LIST.length < 1) {
					//이거 따로 함수로 분리시키면 좋을거같은데
					console.log("공유 웹워커 종료 : 연결된 포트 없음");
                    disconnectConnection("socket is closed by user exit");
					self.close();
                }
                break;
            case "sendMsg":
				console.log(e.data.payload);
                // WEBSOCKET.send(JSON.stringify(e.data.content));
                //웹소켓 연결시에만 받게 해야함

				//reqAct로 분기
				switch(e.data.payload.reqAct) {
					case "chatRoomEnter": //  -> 채팅방 진입 요청
						WEBSOCKET.send(JSON.stringify({
							act: "chatRoomEnter", // <-- 이거 어케 스위치문 없이 통합할 수 없나
							payload: {
								roomidx: e.data.payload.roomidx
							}
						}));
						break;
				}
                break;
			case "chkConnState":
				let state = "";
				if(!!WEBSOCKET) {
					state = "WS";
				} else if(!!EVENTSOURCE) {
					state = "SSE";
				} else {
					state = "NONE";
				}
				port.postMessage({
					actRes: "chkConnStateDone",
					connState: state
				});
				break;
            case "upgradeConn":
				//SSE -> WebSocket
				//웹소켓 연결이 살아있는지 체크 - 중복 연결을 막는다
				if(!WEBSOCKET) {
					//우선 브컨들에게 웹소켓 연결을 알린다
					broadcastMsg("WebSocket Conn req", {
						status: "WebSocket Conn reqed"
					})
					upgradeConnection(port);
				}
				break;
			case "downgradeConn":
				//WebSocket -> SSE
				//SSE 연결이 살아있는지 체크 - 중복 연결을 막는다
				if(!EVENTSOURCE) {
					downgradeConnection("socket is closed by connection downgrade");
				}
				break;
			case "logout":
				disconnectConnection("socket is closed by connection close");
				break;
        }
	};
};

//최초연결시 SSE 연결
if(!EVENTSOURCE && !WEBSOCKET) {
	connectSSE();
}

/*================================================================================*/
/*브로드캐스트*/
function broadcastMsg(by, data) {
	PORT_LIST.forEach((port) => {
		port.postMessage(data);
		console.log("SharedWorker broadcast to=");
		console.log(port);
		console.log("data=");
		console.log(data);
	});
}

/*================================================================================*/
/*연결 관리*/
//연결 업그레이드 함수
function upgradeConnection(reqPort) {
	connectWebSocket().
	then(() => {
		if(!!EVENTSOURCE) {
			EVENTSOURCE.close();
			EVENTSOURCE = null;
		}

		//일단 요청 브컨에게 연결 수립을 알린다
		console.log("upgradeConn postMsg");
		reqPort.postMessage({
			actRes: "WebSocketConnDone"
		});
	})
	.catch((err) => {});
}
//연결 다운그레이드 함수
function downgradeConnection(reason) {
	connectSSE();
	if(!!WEBSOCKET) {
		WEBSOCKET.close(1000, reason);
		WEBSOCKET = null;
	}
}
//연결 종료 함수
function disconnectConnection(reason) {
	if(!!EVENTSOURCE) {
		EVENTSOURCE.close();
		EVENTSOURCE = null;
	}
	if(!!WEBSOCKET) {
		WEBSOCKET.close(1000, reason);
		WEBSOCKET = null;
	}
}

//SSE 연결 함수
function connectSSE() {
	EVENTSOURCE = new EventSource("http://localhost:8080/sse/subscribe");
	
	EVENTSOURCE.addEventListener("open", (e) => {
		console.log("SSE open");
	});
	EVENTSOURCE.addEventListener("connected", (e) => {
		console.log("SSE received msg=connected");
	});
	EVENTSOURCE.addEventListener("broadcast", (e) => {
		console.log("SSE received msg=broadcast");
		broadcastMsg("SSE", e.data);
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
//웹소켓 연결 함수
async function connectWebSocket() {
	WEBSOCKET = new WebSocket("ws://localhost:8080/wschat");
	
	WEBSOCKET.onopen = (e) => {
		console.log("WebSocket open");
	};
	WEBSOCKET.onmessage = (e) => {
		console.log("WebSocket received msg");
		broadcastMsg("WebSocket", e.data);
	};
	WEBSOCKET.onerror = (e) => {
		WEBSOCKET.close();
		console.log("WebSocket onerror=" + e);
	};
	WEBSOCKET.onclose = (e) => {
		console.log("WebSocket closed");
	};

	return new Promise((resolve, reject) => {
		WEBSOCKET.addEventListener("open", (e) => {
			resolve();
		});
	});
}