package com.t1.tripfy.config.handler;

import java.util.HashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
	/* 
	 * 전체 참조
	 * https://devel-repository.tistory.com/39
	 * 대충 전체적인 설명 + 웹소켓 세션 timeout 설정, Postman 웹소켓 테스트
	 * 
	 * 해야하는거
	 * 
	 * 채팅방 세션 객체
	 *  일단 해시맵
	 *  키는 packagenum 은 아님
	 *   키는 chatid로 함
	 *  값은 ChatDTO
	 *   근데 존나 애매한게 이렇게 넣어버리면 요청자가 속한 채팅방을 찾기가 좀 좆같아짐
	 *  하여간에 임마는 연결시마다 DB를 까려면 좀 그러니까 한명
	 * 생각해보니까 어짜피 1대1인데 따로 서버 메모리에 올리는 채팅방 목록같은건 좀 아닌거같음
	 * 
	 * 연결시
	 *  1. 우선 요청링크에서 userid 뜯어내기
	 *   나중에 UUID같은거로 갈아치울 수도 있음
	 *    로그인시 UUID를 할당해주고 JS에 넘긴다거나 뭐
	 *  2. 당연히 유효성 체크
	 *   요청이 로그인된 유저로부터 왔는지 체크
	 *  3. 해당 userid로 개설된 웹소켓 세션이 존재하는지 확인
	 *   (비정상 종료 등으로 인해)존재하는 경우 삭제해준다
	 *    웹소켓 객체도 close 해주고 WEBSOCKET_SESSIONS(HashMap 객체) 에서도 지워줘야 함
	 *  4. 접속한 채팅방 확인 전에 우선 웹소켓 세션을 HashMap에 등록해준다 - 240515
	 *   WEBSOCKET_SESSION.put() ㅇㅇ
	 *  5. 해당 유저가 접속한 채팅이 있는지 확인
	 *   DB 까봐야됨
	 *    대충 채팅방 세션 객체에서 우선 확인할 수도 있고 - 채팅방 세션 객체를 만들거라면
	 *   접속한 채팅 리스트(Chat 테이블)을 useridA(아마 사용자쪽 컬럼)를 뒤져서 다 끌고온다
	 *   또한 읽지 않은 채팅 개수랑 각 채팅방도 가져와야 함
	 *    -> 근데 이걸 하려면 chat, chat_detail 어딘가나 별개의 테이블에 읽지 않은 채팅을 기록해야함
	 *     아마 근데 1대1 한정이라는걸 생각하면 어케 간단하게 될 수도 있고
	 *  6. 가져온 user 의 접속 채팅방과 안 읽은 채팅 개수를 적당히 싸서 보낸다(session.sendMessage())
	 *  
	 *  +추가
	 *   웹소켓은 페이지 이동시에도 끊김
	 *    그리고 아마 탭 삭제, 혹은 이동 같은 경우 "비정상적인 종료"가 될 수도 있음
	 *   그리고 여기서 구현하는 방향은 로그인유저가 페이지를 이동할때마다 onload()로 웹소켓 걸어주기임
	 *   
	 *   그래서 해야하는거 두가지
	 *    웹소켓 연결시 이미 해당 유저의 웹소켓 세션이 존재하는지 파악, 존재시 삭제해주기
	 *    웹소켓 연결중에는 항상 일정 간격으로 핑을 보내서 미응답시 웹소켓 연결이 끊겼다고 간주, 삭제해주기
	 * 
	 *   시발 이거 그냥 평상시 거는건 SSE만 하고 채팅방 열 때만 웹소켓을 거는게 나을지도 모르겠음
	 *    그래도 일단 이거는 보류
	 *   참조
	 *   https://brunch.co.kr/@springboot/801
	 *   (Spring WebSocket Ping/Pong)
	 *    대충 단순히 탭 닫기, 브라우저 닫기, (아마) 페이지 이동시에는 명시적으로 종료 처리가 됨
	 *     긍게 서버도 연결 종료를 알 수 있음
	 *    근데 네트워크 끊김 등의 경우 서버는 연결이 끊겼다는걸 알 수 없음
	 *    그래서 서버단에서 핑을 전송, 클라단에서 퐁이 오면 연결이 살아있음을 확인한다 정도
	 *  
	 * 메시지 발송시
	 *  요청 링크와 메시지를 까서 - 여기서는 userid를 메시지에 싸서 보낼 수도 있음
	 *   누가 어떤 채팅방에 쏜 메시지인지 파악
	 *  그 후 DB 찍턴을 해 해당 채팅방의 유저를 파악함
	 *   해당 유저가 세션에 연결되어있는지 
	 * 
	 * 
	 * #일단 참조
	 * https://velog.io/@l0o0lv/Spring-Boot-WebSocket%EC%9D%84-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%8B%A4%EC%8B%9C%EA%B0%84-%EC%AA%BD%EC%A7%80-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%841
	 * https://developer.mozilla.org/ko/docs/Web/API/WebSockets_API/Writing_WebSocket_client_applications
	 * 
	 * #요청 링크 구성
	 *  ws://localhost:8080/test/{userid}
	 *  
	 * #요청 링크 구성 240515
	 *  채팅방 개설(웹소켓 연결과 동시에) 
	 *   ws://localhost:8080/test/init/{userid}/{packagenum}
	 *  웹소켓 연결 상태에서 채팅방 개설
	 *   이건 걍 JSON으로 처리
	 *  채팅방 연결(일반 웹소켓 연결)
	 *   ws://localhost:8080/test/comm/{userid}
	 * 
	 * #쿼리스트링으로 구성? 240515
	 *  채팅방 개설(웹소켓 연결과 동시에)
	 *   ws://localhost:8080/test?pupo=init&uid={userid}&pkg={packagenum}
	 *  웹소켓 연결 상태에서 채팅방 개설
	 *   위와동일ㅇㅇ
	 *  채팅방 연결(일반 웹소켓 연결)
	 *   ws://localhost:8080/test?pupo=comm&uid={userid}
	 *  
	 * #웹소켓 통신의 종류 관련
	 *  어.. 대충 이정도가 필요해보임
	 *   웹소켓 연결(핸드쉐이크)
	 *    1. 채팅방 개설
	 *    2. 일반 웹소켓 연결
	 *   웹소켓 통신(아마 JSON)
	 *    3. 메시지 불러오기(채팅방 진입시)
	 *    4. 메시지 전송하기(메시지 작성)
	 *    5. 핑퐁 메시지 전송하기(n초마다)
	 *  아마 여기에 추가로
	 *   6. 메시지 삭제
	 *   7. 특수 메시지(막 카톡 블럭?메시지같은 그런, 혹은 이미지 등)
	 *   8. 웹소켓 연결 해제
	 *   이정도?
	 * 
	 * #클라단 JS 웹소켓 관련
	 * https://ko.javascript.info/websocket
	 * https://developer.mozilla.org/ko/docs/Web/API/WebSockets_API/Writing_WebSocket_client_applications
	 * 
	 * #ping/pong 관련
	 *  이걸 서버에서 보내는것과 클라에서 보내는게 있는데
	 *   생각해보니까 클라에서 보내는게 쉬울지도 모르겠음
	 *  그리고
	 *   https://brunch.co.kr/@springboot/801
	 *   (Spring WebSocket Ping/Pong)
	 *    요 친구를 보면 ping/pong 로직 관련 얘기가 있음
	 *     대충 클라가 pong을 못 보낸 상황을 서버에서 어떻게 파악하냐의 얘기인데
	 *     블로그에서는 WebSocketSession에 만료시간을 할당하고
	 *     pong 수신시 갱신해주는 방법을 얘기함
	 *   만료시간 관해서 좀 찾아봤는데
	 *   https://stackoverflow.com/questions/50587573/spring-websocket-is-automatically-closed-after-30-minutes-timeout
	 *   WebSocketSession은 만료시간 관한게 없고
	 *   HttpSession쪽을 만지라... 정도 얘기밖에 없음
	 *    근데 애매한게 WebSocketSession의 HttpSession이 로그인할때 쓰는 HtppSession이랑 같은건지 모름
	 *     다른거면 상관없는데 같은거면...
	 *     일단 WebSocketSession서 꺼내온 HttpSession은 그거 맞긴함
	 *   이게 골아픈게 클라에서 보내건 서버에서 보내건 결국 만료시간 없이 구현하려면
	 *    각 세션별로 스레드를 다 파주고 wait인지 뭐시깽인지를 박아줘야 함
	 *    심지어 이거 스레드 파면 스프링 안쪽이랑 부딛칠지도 모름
	 *   참조
	 *   	https://velog.io/@umtuk/%EB%A0%88%EC%8B%9C%ED%94%BC-5-4-%EC%9B%B9%EC%86%8C%EC%BC%93
	 *       STOMP 쓰는거긴 한데 앞부분에 timeout 설정 얘기가 있음
	 *      https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/socket/server/standard/ServletServerContainerFactoryBean.html
	 *       일단 위에서 얘기한 ServletServerContainerFactoryBean 임마
	 *       FactoryBean<WebSocketContainer>를 상속하긴함
	 * 
	 * #메시지 전달 관련
	 *  이건 좀 사족일 수도 있지만...
	 *   만약 클라의 네트워크가 순간 끊어진 순간 서버서 메시지가 보내졌다면?
	 *    거꾸로도 당연히 가능할거고
	 *   그래서 생각한게
	 *    수신자가 수신 성공여부를 다시 발송해주는게 어떨까 싶음
	 *     성공시: 서버서 보냄 -> 클라가 받고 성공여부 전송 -> 서버서 확인
	 *     실패시: 서버서 보냄 -> ... -> 일정시간 후(스레드 파고 sleep 혹은 Timer/TimerTask)
	 *                       반복... <- 재전송
	 *    아마 이 로직은 별개의 클래스나 아마 @Bean으로 박는게 맞을까 싶음
	 *    근데 사족임 사족
	 * 
	 * #채팅방 개설 관련
	 *  생각해보니까
	 *   존재하는 채팅방에 들어가는건 채팅 버튼으로 하면 되는데
	 *   채팅방을 만드는거는 별도의 버튼 - 아마 개별 패키지 페이지에서 해야 할 거임
	 *   이걸 구현하는 방법은 아마 두가지가 있을거임
	 *    1. 채팅 시작 버튼 클릭시 웹소켓을 열고 관련 메시지(패키지 번호, userid 등) 보내기
	 *     a. 근데 웹소켓이 이미 열려있을 수도 있음(채팅버튼 클릭시)
	 *      i.  클라단에서 JS로 웹소켓 연결상태 전역변수를 두기
	 *      ii. 해당 변수를 체크해서 웹소켓이 이미 열린경우 그냥 관련 메시지만 보내기
	 *     b. 어찌됐건 버튼이 눌렸으면 웹소켓 연결과 동시에 채팅창도 열려야 함
	 *     c. 연결여부 체크도 뭐 해야됨dd
	 *    2. 채팅 시작 버튼 클릭시 AJAX로 관련메시지 보내고 개설 확인(응답메시지)시 웹소켓 열기
	 *     a. 오바임
	 *  일단 1번으로 가야할듯 한데 이거가 좀 애매한게
	 *   현재 링크 방식(ws://localhost:8080/test/{userid})은 좀...
	 *   값을 여러개 보내기가 애매함
	 *    채팅방을 생성한다면 값을 두개를 보내야 함
	 *     생성자(일반사용자) userid, 생성하려는 채팅방 패키지넘버 packagenum
	 *    그럼 링크를 막 /test/init/{packagenum}/{userid} 이딴식으로 보내야한다는건데
	 *    이럴바에는 걍 쿼리스트링으로 보내고 UriComponents로 까는게 나을지도 모르겠음
	 *     일단 지금은 기존 링크대로 감 - 240515
	 * 
	 * #UUID 관련
	 *  대충 세가지 방법이 잇는거가틈
	 *   1. 걍 매 페이지 로딩시마다 타임리프로 JS 변수로 넘기기
	 *   2. 쿠키로 넘기기
	 *    a. 채팅 쪽에서는 핸드쉐이크할때나 JSON으로 보낼 때 JS로 UUID를 꺼내서 담아 보내는거임
	 *   3. 세션에 저장
	 *    a. 지금은 loginUser=userid 형식인데 이걸 loginUser=UUID로 하는거임
	 *     i. 좀 애매한게 UUID는 클라에서도 알아야 하니까 세션이랑은 좀 맞지가 않음
	 * */
	
	//임마는 유저 <-> 서버간 1대1 세션(WebSocketSession 객체)를 저장하는 친구
	// 최소한 내가 이해하기로는 WebSocketSession 객체는 HttpSession처럼 각 유저별로 따로 존재함
	// WebSocketSession 임마로 sendMessage()등을 할 수 있으니 이렇게 따로 모아놓았다가
	// 메시지 전달(handleTextMessage) 시 임마를 기준으로 뿌려준다
	// 또한 ping/pong 시에도 임마를 순회하면서 쏴준다
	private static final HashMap<String, WebSocketSession> WEBSOCKET_SESSIONS = new HashMap<>();

	// 웹소켓 연결시
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
	//1. 요청 uri에서 userid 뽑기
		// 이걸 /test/{userid} 형식 대신 getparameter로 넣고 UriComponents? 를 쓸 수도 있음
		String splitUri[] = session.getUri().toString().split("/");
		String userid = splitUri[splitUri.length - 1];
		
	//2. 유효성 검사
		//요청링크 유효성 검사
		// {userid}가 비어있으면 연결을 끊고 메서드 종료
		if(userid == null || userid.isBlank()) {
			session.close(CloseStatus.PROTOCOL_ERROR);
			return;
		}
		//요청 userid가 로그인 한 유저인지 확인
		// 요청 userid가 해당 클라이언트 HttpSession의 loginUser값과 다르면 연결을 끊고 메서드 종료
		if(!session.getAttributes().get("loginUser").equals(userid)) {
			session.close(CloseStatus.PROTOCOL_ERROR);
			return;
		}
		
	//3. 웹소켓 기존 연결이 존재하는지 확인
		// 기존의 웹소켓 연결(WebSocketSession)이 살아있으면 끊어준다
		if(WEBSOCKET_SESSIONS.containsKey(userid)) {
			WebSocketSession tempSess = WEBSOCKET_SESSIONS.remove(userid);
			if(tempSess != null) {
				tempSess.close(CloseStatus.SESSION_NOT_RELIABLE);
			}
		}
		
	//4. 웹소켓 등록
		WEBSOCKET_SESSIONS.put(userid, session);
		
	//5. userid 가 접속해있는 채팅 확인하고 끌고오기
		// ChatServiceImpl가 필요함
	
	//6. 채팅방 리스트와 안 읽은 채팅 개수 보내주기
		// wip
	}

	// 메시지 발송시
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
	}

	// 웹소켓 연결 해제시
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		
	}
}