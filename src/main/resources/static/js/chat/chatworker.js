/*
	채팅 통합 처리를 위한 SharedWorker
	여기 로그들은
	chrome://inspect/#workers
	로 가야만 볼 수 있음
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
            case "closePort":
                let idx;
                for(let i = 0; i < PORT_LIST.length; i++) {
                    if(PORT_LIST[i] == port) {
                        idx = i;
                    }
                }
                PORT_LIST.splice(idx, 1);
                if(PORT_LIST.length < 1) {
					//이거 따로 함수로 분리시키면 좋을거같은데
					console.log("공유 웹워커 종료 : 연결된 포트 없음");
					if(!!WEBSOCKET)
						WEBSOCKET.close(1000, "socket is closed by user");
					if(!!EVENTSOURCE)
						EVENTSOURCE.close();
                    self.close();
                }
                break;
            case "sendMsg":
                //socket.send(e.data.content);
                //웹소켓 연결시에만 받게 해야함
                break;
            case "upgradeConn":
				//SSE -> WebSocket
				connectWebSocket();
				EVENTSOURCE.close();
				break;
			case "downgradeConn":
				//WebSocket -> SSE
				connectSSE();
				WEBSOCKET.close(1000, "socket is closed by connection downgrade");
				break;
			case "logout":
				break;
        }
	};
};

//최초연결시 SSE 연결
if(!EVENTSOURCE && !WEBSOCKET) {
	connectSSE();
}

//SSE 연결 함수
function connectSSE() {
	EVENTSOURCE = new EventSource("http://localhost:8080/sse/subscribe");
	
	EVENTSOURCE.addEventListener("open", (e) => {
		
	});
	EVENTSOURCE.addEventListener("connected", (e) => {
		
	});
	EVENTSOURCE.addEventListener("broadcast", (e) => {
		PORT_LIST.forEach((port) => {
			port.postMessage(e.data);
		})
	});
	EVENTSOURCE.onerror = (err) => {
		
	};
	EVENTSOURCE.onclose = () => {
		
	};
}
//웹소켓 연결 함수
function connectWebSocket() {
	WEBSOCKET = new WebSocket("ws://localhost:8080/wschat");
	
	WEBSOCKET.onopen = (e) => {
		
	};
	WEBSOCKET.onmessage = (e) => {
		PORT_LIST.forEach((port) => {
			port.postMessage(e.data);
		});
	};
	WEBSOCKET.onerror = (e) => {
		
	};
	WEBSOCKET.onclose = (e) => {
		
	};
}