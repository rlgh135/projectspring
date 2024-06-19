/**
    일반 <-> 일반 유저 채팅 생성 함수

    아마 /board/get 쪽 채팅 버튼 이벤트리스너 함수 안에서 호출해주면 될 것임

    서버단에서 HttpSession로 꺼내서 처리하기에 요청자(개설자) userid는 필요 없음

    상대 유저(targetUserid) 와 이미 1대1 채팅이 있으면
    새로 채팅방을 개설하는 대신 기존 채팅방을 연다
    
    반환값은 없음

    async가 붙어있어서 아마 Promise를 반환하기는 할 건데 받을 필요는 없음

    @param {string} targetUserid 채팅방 개설시 초대할 유저의 userid
    @returns {Promise<*>} 이거 받을 필요 없음
*/
async function createUserToUserChat(targetUserid) {
    console.log("createUserToUserChat(), targetUserid=" + targetUserid);

    //CHAT_OT*_VD 로드 체크
    if(!IS_VD_LOADED) {
        //로드되어있지 않으면 로드해준다
        console.log("VD가 로드되지 않았음, IS_VD_LOADED=" + IS_VD_LOADED);
        let crList;
        try {
            //List<ChatListPayloadDTO> 수신
            crList = await ajaxGet("localhost:8080", "/chat");
            console.log("ajaxGet('localhost:8080/chat') 성공, crList=");
            console.log(crList);
        } catch(err) {
            //ajax 실패시

            /* 실패 처리 */

            console.log("ajaxGet('localhost:8080/chat') 실패, err=");
            console.error(err);
            console.log("createUserToUserChat() 종료");
            return;
        }

        //VD, DOM 처리(chatWindowBtnClick() 참조)
        setChatListVD(crList);
        printChatList(crList);
        IS_VD_LOADED = true;
        console.log("createUserToUserChat() DOM 적용됨");
    }
    console.log(CHAT_OTO_VD);
    //상대 유저(targetUserid) 와의 1대1 채팅이 이미 존재하는지 체크
    /*
        이제는 단순 OTO 여부만 확인하는게 아니라
        roomType=0? 과 isTerminated=false 도 체크함
    */
    let tgtRoomIdx = null;
    let tgtVDIdx = null;
    let isChatRoomAlreadyExist = false;
    if(CHAT_OTO_VD.length > 0) {
        for(let i = 0; i < CHAT_OTO_VD.length; i++) {
            if(
                CHAT_OTO_VD[i].dataObj.roomType === 0
                &&
                !CHAT_OTO_VD[i].dataObj.isTerminated
                &&
                CHAT_OTO_VD[i].dataObj.userList[0].userid === targetUserid
            ) {
                //이미 존재하는 경우
                tgtRoomIdx = CHAT_OTO_VD[i].dataObj.roomidx;
                tgtVDIdx = i;
                isChatRoomAlreadyExist = true;
                break;
            }
        }
    }

    console.log("채팅방" + isChatRoomAlreadyExist ? "이 이미 존재함" : "이 없음" + ", isChatRoomAlreadyExist=" + isChatRoomAlreadyExist);

    //채팅방 생성 분기
    if(!isChatRoomAlreadyExist) {
        //채팅 생성
        console.log("채팅방 생성");
        let cr;
        try {
            //ChatListPayloadDTO 수신
            cr = await ajaxPost("localhost:8080", "/chat", {
                chatRoomType: 0,
                title: null,
                invitee: [
                    targetUserid
                ]
            });
            console.log("ajaxPost('localhost:8080/chat') 성공, cr=");
            console.log(cr);
        } catch(err) {
            //ajax 실패시

            /* 실패 처리 */

            console.log("ajaxPost('localhost:8080/chat') 실패, err=");
            console.error(err);
            console.log("createUserToUserChat() 종료");
            return;
        }

        //수신한 ChatListPayloadDTO를 VD에 적용
        insertChatListVD(cr);
        //VD로 DOM 수정
        adjustChatList();
        //roomidx 전달, VD 인덱스 초기화
        tgtRoomIdx = cr.roomidx;
        tgtVDIdx = 0;
    }

    //채팅 오픈 요청
    WORKER.port.postMessage({
        action: "chatRoomEnter",
        payload: {
            roomidx: tgtRoomIdx
        }
    });
    //요청 응답 대기/처리
    try {
        const cb = await promised_waitTillResponse("chatRoomEnter");

        //모달 구성, 미확인 메시지 값 수정(chatListOTNElementClick() 참조)
        setChatBodyInfo(cb.payload);
        printChatRoom(cb.payload);

        //cc-hidden 컨트롤
        open_CHAT_WINDOW();
        open_CHAT_LIST_OTO();
        close_CHAT_LIST_CONT();
        open_CHAT_BODY_CONT();

        //미확인 메시지 값 수정
        UNCHECKED_MSG_COUNT -= CHAT_BODY_INFO.chatRoomInfo.uncheckedmsg;
        CHAT_OTO_VD[tgtVDIdx].uncheckedmsg = 0;

        //아래로 스크롤
        /*
            CBI.isEmpty가 null일 수도 있어서 false를 명시함
        */
        if(CHAT_BODY_INFO.isEmpty == false) {
            scrollIntoLastChatElement();
        }
    } catch(err) {
        //채팅 오픈 실패

        /* 실패 처리 */

        console.log("action: chatRoomEnter 실패, err=");
        console.error(err);
        console.log("createUserToUserChat() 종료");
        return;
    }

    console.log("createUserToUserChat() 완료");
}