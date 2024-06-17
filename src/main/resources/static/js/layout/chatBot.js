let firstChatBotOpen = 0;
const chatBotArea = $('#chatBot-window');
const messageArea = $(".message-area");
$(document).ready(function() {

    $(document).on('click', '.chatBotOn', function() {
        scrollToBottom();
        if(firstChatBotOpen == 0){
            displayTypingIndicator();
            setTimeout(function() {
				scrollToBottom();
                hideTypingIndicator();
                showChatBotMessage("안녕하세요 트리피 입니다 궁금하신 태그를 선택해주세요", "트리피는 뭐하는 곳 인가요?", "가이드는 뭐가 다른가요?", "패키지 신청에 제약이 있나요?");
            }, 1500);
            firstChatBotOpen++;
            scrollToBottom();
        }
        chatBotArea.removeClass("cb-hidden");
        $(this).removeClass("chatBotOn").addClass("chatBotOff");
    });

    $(document).on('click', '.chatBotOff', function() {
        chatBotArea.addClass("cb-hidden");
        $(this).removeClass("chatBotOff").addClass("chatBotOn");
    });

    $(document).on('click', '.select', function(){
        const selectText = $(this).find("p").text().trim();
        showQuestionDom(selectText);
        displayTypingIndicator();
        setTimeout(function() {
            hideTypingIndicator();
            handleSelect(selectText);
        }, 1500);
        $(this).closest(".select-box").remove();
    });

    function displayTypingIndicator() {
        const typingIndicator = $('<div class="typing-indicator"></div>');
        const dots = $('<div class="dots"></div>');
        for (let i = 0; i < 3; i++) {
            const dot = $('<div class="dot"></div>');
            dots.append(dot);
            dot.delay(i * 300).fadeIn(300).fadeOut(300);
        }
        typingIndicator.append(dots);
        messageArea.append(typingIndicator);
    }

    function hideTypingIndicator() {
        $(".typing-indicator").remove();
    }

    function handleSelect(selectText) {
        let msg = "";
        let select1 = "";
        let select2 = "";
        let select3 = "";

        switch(selectText) {
            case "트리피는 뭐하는 곳 인가요?":
                msg = "트리피는 자신이 직접 가이드가 돼서 모두에게 여행지를 추천 및 여행코스를 패키지화 해서 같이 갈 수 있는곳 입니다";
                select1 = "패키지란 무엇인가요?";
                select2 = "다른 기능은 없나요?";
                select3 = "처음으로 돌아가기";
                break;
            case "가이드는 뭐가 다른가요?":
                msg = "가이드란 본인이 만든 여행코스를 남들에게 추천하고 판매 할 수 있습니다";
                select1 = "가이드 자격은 어떻게 되나요?";
                select2 = "가이드는 어떤 혜택이 있나요?";
                select3 = "처음으로 돌아가기";
                break;
            case "패키지 신청에 제약이 있나요?":
                msg = "패키지 신청에는 특정한 제약이 없습니다. 누구나 신청할 수 있으며, 패키지의 세부 사항은 각 패키지 페이지에서 확인할 수 있습니다.";
                select1 = "패키지 신청 방법은?";
                select2 = "패키지 신청 후 변경이나 취소가 가능한가요?";
                select3 = "처음으로 돌아가기";
                break;
            // 트리피는 뭐하는 곳 인가요 연결
            case "패키지란 무엇인가요?":
                msg = "가이드가 된 유저가 자신이 여행해보거나 여행 계획을 패키지화 해서 다른 유저들에게 공유 및 같이 가기위한 여행계획 입니다.";
                select1 = "사기면 어떡하나요?";
                select2 = "유료인가요?";
                select3 = "처음으로 돌아가기";
                break;
            case "사기면 어떡하나요?":
                msg = "사기를 대비해서 신고기능이 존재합니다. 가이드들이 만든 패키지를 보고 이상하다 싶으면 신고 부탁드립니다";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            case "유료인가요?":
                msg = "네 유료입니다 여행비용에 포함하는 금액과 가이드에게 일정에 수수료가 지불됩니다!";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            case "다른 기능은 없나요?":
                msg = "트리피에서 운영하는 여행 커뮤니티도 있습니다. 많은 유저들이 지역에 관련된 글이나 여행 동반 할 사람을 구하는 게시판도 존재합니다";
                select1 = "여행 동반이란 어떤건가요?";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            case "여행 동반이란 어떤건가요?":
                msg = "누군가와 같이 여행하고 싶을때 게시판에 글을 남겨서 원하는 유저와 같이 동반해서 여행을 가는 시스템입니다";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            // 가이드는 뭐가 다른가요? 연결
            case "가이드 자격은 어떻게 되나요?":
                msg = "가이드 자격은 따로 없습니다! 모두가 가이드가 될 수 있습니다!";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            case "가이드는 어떤 혜택이 있나요?":
                msg = "가이드는 자신이 원하는 패키지를 일정의 수수료를 받고 안내할 수 있습니다! 여행도가고 소소한 소득도 있지요";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            //패키지 신청에 제약이 있나요? 연결
            case "패키지 신청 방법은?":
                msg = "어렵지 않습니다 자신이 원하는 패키지를 발견 후 인원에 맞춰 결제하시면 됩니다!";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            case "패키지 신청 후 변경이나 취소가 가능한가요?":
                msg = "가능합니다! 하지만 당일취소는 불가능하니 기간에 맞춰 취소해주세요!";
                select1 = "";
                select2 = "";
                select3 = "처음으로 돌아가기";
                break;
            //처음으로 돌아가기
            case "처음으로 돌아가기":
                msg = "안녕하세요 트리피 입니다 궁금하신 태그를 선택해주세요";
                select1 = "트리피는 뭐하는 곳 인가요?";
                select2 = "가이드는 뭐가 다른가요?";
                select3 = "패키지 신청에 제약이 있나요?";
                break;
        }
        showChatBotMessage(msg, select1, select2, select3);
        scrollToBottom();
    }

    function showChatBotMessage(msg, select1, select2, select3) {
        let ChatBotMsgDom = `
        <div class="chatBot-message">
            <div class="msg-time">${getCurrentTime()}</div>
            <div class="msg">${msg}</div>
            <div class="select-box">`;
        if(select1 !== "") {
            ChatBotMsgDom += `<div class="select"><p>${select1}</p></div>`;
        }
        if(select2 !== "") {
            ChatBotMsgDom += `<div class="select"><p>${select2}</p></div>`;
        }
        if(select3 !== "") {
            ChatBotMsgDom += `<div class="select"><p>${select3}</p></div>`;
        }
        ChatBotMsgDom += `</div></div>`;
        messageArea.append(ChatBotMsgDom);
    }

    function showQuestionDom(question){
        let questionDom =`
        <div class="question-box">
        <div class="msg-time">${getCurrentTime()}</div>
        <div class="question-content">${question}</div>
    </div>
        `;
        messageArea.append(questionDom);
    }

    function getCurrentTime() {
        // 현재 시간을 가져옴
        const now = new Date();
      
        // 시간과 분을 구함
        let hours = now.getHours();
        const minutes = now.getMinutes();
      
        // 오전/오후를 결정
        const period = hours >= 12 ? '오후' : '오전';
      
        // 12시간 형식으로 변환
        hours = hours % 12;
        hours = hours ? hours : 12; // 0시를 12시로 변환
      
        // 분을 두 자리 숫자로 맞춤
        const minutesFormatted = minutes < 10 ? '0' + minutes : minutes;
      
        // 최종 문자열 생성
        const timeString = `${period} ${hours}:${minutesFormatted}`;
      
        return timeString;
    }

    function scrollToBottom() {
        messageArea.animate({ scrollTop: messageArea.prop("scrollHeight") }, 500);
    }
});
