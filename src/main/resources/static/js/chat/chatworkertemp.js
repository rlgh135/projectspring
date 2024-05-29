/*
	채팅 통합 처리를 위한 SharedWorker
	여기 로그들은
	chrome://inspect/#workers
	로 가야만 볼 수 있음
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
				console.log(e.data);
				console.log(e.data.content);
                WEBSOCKET.send(e.data.content);
                //웹소켓 연결시에만 받게 해야함
                break;
			case "logout":
                PORT_LIST = null;
				disconnectConnection("socket is closed by connection close");
                self.close();
				break;
        }
	};
};

//최초연결시 웹소켓 연결
if(!WEBSOCKET) {
    connectWebSocket();
}

/*================================================================================*/
/*브로드캐스트*/
function broadcastMsg(by, data) {
	PORT_LIST.forEach((port) => {
		port.postMessage(data);
		console.log("SharedWorker broadcast to=" + port + " data=" + data);
	});
}

/*================================================================================*/
/*연결 관리*/
//연결 종료 함수
function disconnectConnection(reason) {
	if(!!WEBSOCKET) {
		WEBSOCKET.close(1000, reason);
		WEBSOCKET = null;
	}
}

//웹소켓 연결 함수
function connectWebSocket() {
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
}