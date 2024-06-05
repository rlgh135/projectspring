$(document).ready(function() {
    const modal = $('#modalContainer');
    const modalOpenButton = $('#modalOpenButton');
    const $calendar = $('#calendar-calendar');
    let $activeInput = null;
    const today = new Date();
    today.setDate(today.getDate() + 7);
    //돔구현
    //국내,해외선택
    const modalContent1 = `
    <div id="modalContent">
        <div class="head_area">
            <div class="head_text">국/해외 선택<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>어디로<br>가시나요?</h2>
        </div>
        <div class="body_area">
            <div class="dno">
                <button class="country domestic">국내</button>
                <button class="country overseas">해외</button>
            </div>
        </div>
    </div>`;
    //국내선택시 돔
    const modalContent2 =`
    <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="c1"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">지역 선택<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>어디로<br>가시나요?</h2>
        </div>
        <div class="body_area">
            <div class="dno">
                <button class="region Gangwon">제주도</button>
                <button class="region">서울</button>
                <button class="region">경기도</button>
                <button class="region">강원도</button>
                <button class="region">충정도</button>
                <button class="region">경상도</button>
                <button class="region">전라도</button>
                <button class="region">인청광역시</button>
            </div>
        </div>
    </div>`;
    //해외구현할거
    const modalContentOverseas =`
    <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="c1"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">지역 선택<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>어디로<br>가시나요?</h2>
        </div>
        <div class="body_area">
            <div class="dno">
                <button class="country overseas" data-country="ea">동남아</button>
                <button class="country overseas" data-country="gu">괌/사이판/호주/뉴질랜드</button>
                <button class="country overseas" data-country="jp">일본</button>
                <button class="country overseas" data-country="cn">중국</button>
                <button class="country overseas" data-country="eu">유럽</button>
                <button class="country overseas" data-country="us">하와이/미국/중남미</button>
            </div>
        </div>
    </div>`;
    //해외 지역들 구현해야함
    const modalContentOverSeaRegion = {
        "ea": `
	    <div id="modalContent">
	        <div class="head_area">
	            <div class="backButton" id="cor"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
	            <div class="head_text">지역 선택<span>(필수)</span></div>
	            <div id="modalCloseButton">
	                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
	            </div>
	        </div>         
	        <div class="text_area">
	            <h2>어디로<br>가시나요?</h2>
	        </div>
	        <div class="body_area">
	            <div class="dno">
	                <button type="button" class="overregion" data-overregion="태국">태국</button>
	                <button type="button" class="overregion" data-overregion="베트남">베트남</button>
	                <button type="button" class="overregion" data-overregion="필리핀">필리핀</button>
	                <button type="button" class="overregion" data-overregion="싱가폴">싱가폴</button>
	                <button type="button" class="overregion" data-overregion="말레이시아">말레이시아</button>
	                <button type="button" class="overregion" data-overregion="캄보디아">캄보디아</button>
	         	</div>
	        </div>
	    </div>`,
        "gu": `
        <div id="modalContent">
	        <div class="head_area">
	            <div class="backButton" id="cor"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
	            <div class="head_text">지역 선택<span>(필수)</span></div>
	            <div id="modalCloseButton">
	                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
	            </div>
	        </div>         
	        <div class="text_area">
	            <h2>어디로<br>가시나요?</h2>
	        </div>
	        <div class="body_area">
	            <div class="dno">
	                <button type="button" class="overregion" data-overregion="괌">괌</button>
	                <button type="button" class="overregion" data-overregion="사이판">사이판</button>
	                <button type="button" class="overregion" data-overregion="호주">호주</button>
	                <button type="button" class="overregion" data-overregion="뉴질랜드">뉴질랜드</button>
	            </div>
	        </div>
    	</div>`,
        "jp": `
        <div id="modalContent">
	        <div class="head_area">
	            <div class="backButton" id="cor"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
	            <div class="head_text">지역 선택<span>(필수)</span></div>
	            <div id="modalCloseButton">
	                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
	            </div>
	        </div>         
	        <div class="text_area">
	            <h2>어디로<br>가시나요?</h2>
	        </div>
	        <div class="body_area">
	            <div class="dno">
	                <button type="button" class="overregion" data-overregion="오사카">오사카</button>
	                <button type="button" class="overregion" data-overregion="캄보디아">후쿠오카</button>
	                <button type="button" class="overregion" data-overregion="도쿄">도쿄</button>
	                <button type="button" class="overregion" data-overregion="교토">교토</button>
	                <button type="button" class="overregion" data-overregion="삿포로">삿포로</button>
	            </div>
	        </div>
    	</div>`,
        "cn": `
        <div id="modalContent">
	        <div class="head_area">
	            <div class="backButton" id="cor"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
	            <div class="head_text">지역 선택<span>(필수)</span></div>
	            <div id="modalCloseButton">
	                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
	            </div>
	        </div>         
	        <div class="text_area">
	            <h2>어디로<br>가시나요?</h2>
	        </div>
	        <div class="body_area">
	        	<div class="dno">
	                <button type="button" class="overregion" data-overregion="하이난">하이난</button>
	                <button type="button" class="overregion" data-overregion="청도">청도</button>
	                <button type="button" class="overregion" data-overregion="베이징">베이징</button>
	                <button type="button" class="overregion" data-overregion="장가계">장가계</button>
	            </div>
	        </div>
    	</div>`,
        "eu": `
		<div id="modalContent">
	        <div class="head_area">
	            <div class="backButton" id="cor"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
	            <div class="head_text">지역 선택<span>(필수)</span></div>
	            <div id="modalCloseButton">
	                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
	            </div>
	        </div>         
	        <div class="text_area">
	            <h2>어디로<br>가시나요?</h2>
	        </div>
	        <div class="body_area">
	        	<div class="dno">
	                <button type="button" class="overregion" data-overregion="프랑스">프랑스</button>
	                <button type="button" class="overregion" data-overregion="영국">영국</button>
	                <button type="button" class="overregion" data-overregion="이탈리아">이탈리아</button>
	                <button type="button" class="overregion" data-overregion="스페인">스페인</button>
	                <button type="button" class="overregion" data-overregion="독일">독일</button>
	                <button type="button" class="overregion" data-overregion="스위스">스위스</button>
	                <button type="button" class="overregion" data-overregion="핀란드">핀란드</button>
	                <button type="button" class="overregion" data-overregion="노르웨이">노르웨이</button>
	                <button type="button" class="overregion" data-overregion="그리스">그리스</button>
	                <button type="button" class="overregion" data-overregion="포르투갈">포르투갈</button>
	            </div>
	        </div>
    	</div>`,
        "us": `
		<div id="modalContent">
	        <div class="head_area">
	            <div class="backButton" id="cor"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
	            <div class="head_text">지역 선택<span>(필수)</span></div>
	            <div id="modalCloseButton">
	                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
	            </div>
	        </div>         
	        <div class="text_area">
	            <h2>어디로<br>가시나요?</h2>
	        </div>
	        <div class="body_area">
	        	<div class="dno">
	                <button type="button" class="overregion" data-overregion="하와이">하와이</button>
	                <button type="button" class="overregion" data-overregion="미동부">미 동부</button>
	                <button type="button" class="overregion" data-overregion="미서부">미 서부</button>
	                <button type="button" class="overregion" data-overregion="캐나다">캐나다</button>
	                <button type="button" class="overregion" data-overregion="중남미">중남미</button>
	            </div>
	        </div>
    	</div>`
    };
    //달력 돔
    const modalContent3 =`
    <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="c2"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">날짜 선택<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>언제<br>가시나요?</h2>
        </div>
        <div class="body_area" style="margin-top:50px;">
            <div>
            	<input type="text" class="calendar-dateInput" id="calendar-startdate" placeholder="날짜를 선택하세요" readonly>
    			<input type="text" class="calendar-dateInput" id="calendar-enddate" placeholder="날짜를 선택하세요" readonly>
            </div>
            <div class="nxt_btn_area">
              <p class="notice"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10" fill="#1976D2"></circle><path d="M11.2485 14.0939H12.7336L13.0422 7.88978L13.1194 6.05957H10.8821L10.9399 7.88978L11.2485 14.0939ZM11.9814 17.9404C12.8301 17.9404 13.4858 17.4286 13.4858 16.6996C13.4858 15.9706 12.8301 15.4278 11.9814 15.4278C11.1521 15.4278 10.5156 15.9706 10.5156 16.6996C10.5156 17.4286 11.1521 17.9404 11.9814 17.9404Z" fill="white"></path></svg>종료일은 시작일보다 이전일 수 없습니다</p>
              <button class="nxt_btn" id="nextButton">다음</button>
            </div>
        </div>
    </div>`;
    //해달력 돔
    const modalContentovercal =`
    <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="covercal"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">날짜 선택<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>언제<br>가시나요?</h2>
        </div>
        <div class="body_area" style="margin-top:50px;">
            <div>
            	<input type="text" class="calendar-dateInput" id="calendar-startdate" placeholder="날짜를 선택하세요" readonly>
    			<input type="text" class="calendar-dateInput" id="calendar-enddate" placeholder="날짜를 선택하세요" readonly>
            </div>
            <div class="nxt_btn_area">
              <p class="notice"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10" fill="#1976D2"></circle><path d="M11.2485 14.0939H12.7336L13.0422 7.88978L13.1194 6.05957H10.8821L10.9399 7.88978L11.2485 14.0939ZM11.9814 17.9404C12.8301 17.9404 13.4858 17.4286 13.4858 16.6996C13.4858 15.9706 12.8301 15.4278 11.9814 15.4278C11.1521 15.4278 10.5156 15.9706 10.5156 16.6996C10.5156 17.4286 11.1521 17.9404 11.9814 17.9404Z" fill="white"></path></svg>종료일은 시작일보다 이전일 수 없습니다</p>
              <button class="nxt_btn" id="nextButton">다음</button>
            </div>
        </div>
    </div>`;
    //게시글 제목 및 게시글 간단 내용
    const modalContent4 =`
    <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="c3"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">패키지 제목 및 내용<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>패키지 제목과<br>내용을 적어주세요</h2>
        </div>
        <div class="body_area">
            <div class="title_area">
            <input type="text" name="pac_title" id="pac_title" placeholder="제목을 입력해주세요(5글자 이상, 20글자 이내)">
            <p>(<span class="title_length"></span>/20)</p>
            </div>
            <div class="content_area">
            <textarea id="pac_content" name="pac_content" rows="8" cols="50" placeholder="내용을 입력해주세요(10글자 이상, 100글자 이내)"></textarea>
            <p>(<span class="content_length"></span>/100)</p>
            </div>
            <div class="nxt_btn_area">
              <button class="nxt_btn" id="nextButton2">다음</button>
            </div>
        </div>
    </div>`;
    //max인원, 성인가격, 아동가격
    const modalContent5 =`
      <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="c4"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">인원 가격<span>(필수)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>인원과<br>가격을 지정해주세요</h2>
        </div>
        <div class="body_area">
            <div class="personal_area">
                <div class="personal_label">최대 인원</div>
                <select name="personal" id="personal">
                    <option disabled selected>인원 수</option>
                </select>
            </div>         
            <div class="price_area">
                <div class="adult_price">성인 가격</div>
                <input type="text" name="a_price" id="a_price" placeholder="성인 가격을 입력해주세요">
            </div>
            <div class="price_area">
                <div class="child_price">아동 가격</div>
                <input type="text" name="c_price" id="c_price" placeholder="아동 가격을 입력해주세요">
            </div>
            <div style="text-align: center; font-size: 12px;">
                <p class="warning_text"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10" fill="#1976D2"></circle><path d="M11.2485 14.0939H12.7336L13.0422 7.88978L13.1194 6.05957H10.8821L10.9399 7.88978L11.2485 14.0939ZM11.9814 17.9404C12.8301 17.9404 13.4858 17.4286 13.4858 16.6996C13.4858 15.9706 12.8301 15.4278 11.9814 15.4278C11.1521 15.4278 10.5156 15.9706 10.5156 16.6996C10.5156 17.4286 11.1521 17.9404 11.9814 17.9404Z" fill="white"></path></svg>숫자는 21억 이하로 적어주세요</p>
            </div>
            <div class="nxt_btn_area">
                <button class="nxt_btn" id="nextButton3">다음</button>
            </div>
        </div>
    </div>`;
    //사진 추가
    const modalContent6 = `
    <div id="modalContent">
        <div class="head_area">
            <div class="backButton" id="c5"><svg width="30" height="30" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" style="transform: rotate(0deg); cursor: pointer;"><path d="M16 3L7 12L16 21" stroke="#000" stroke-width="1.5"></path></svg></div>
            <div class="head_text">배너 사진 추가<span>(선택)</span></div>
            <div id="modalCloseButton">
                <svg width="24" height="24" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="#000"></path><path d="M12 1.00098L1 12.001" stroke="#000"></path></svg>
            </div>
        </div>         
        <div class="text_area">
            <h2>사진을<br>추가해 보세요</h2>
        </div>
        <div class="body_area">
            <div class="thumnail_area">
                <img src="/images/packageimg/tripfy_banner.png" alt="image" class="thumnail_img">
                <div class="cancelFile"><svg width="11" height="11" viewBox="0 0 13 13" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M1 1.00098L12 12.001" stroke="rgba(154, 154, 154, 1)"></path><path d="M12 1.00098L1 12.001" stroke="rgba(154, 154, 154, 1)"></path></svg></div>
                <div class="thumnail">
                    <svg width="40" height="40" viewBox="0 0 24 24" fill="white" xmlns="http://www.w3.org/2000/svg">
                        <path d="M22 15C22 20 20 22 15 22H9C4 22 2 20 2 15V9C2 4 4 2 9 2H15C20 2 22 4 22 9V15ZM22 15L18.7699 13.1858C18.0373 12.7743 17.1257 12.8646 16.488 13.4118L13.3899 16.0705C12.6099 16.7405 11.3499 16.7405 10.5699 16.0705L10.2399 15.7805C9.52992 15.1705 8.38992 15.1105 7.59992 15.6405L2.66992 18.9505M11 8C11 9.10457 10.1046 10 9 10C7.89543 10 7 9.10457 7 8C7 6.89543 7.89543 6 9 6C10.1046 6 11 6.89543 11 8Z" stroke="#1F2023" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <p class="add_img_txt">사진을 선택하세요!</p>         
                </div>
            </div>
            <div class="thumnail_notice_area">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10" fill="#1976D2"></circle><path d="M11.2485 14.0939H12.7336L13.0422 7.88978L13.1194 6.05957H10.8821L10.9399 7.88978L11.2485 14.0939ZM11.9814 17.9404C12.8301 17.9404 13.4858 17.4286 13.4858 16.6996C13.4858 15.9706 12.8301 15.4278 11.9814 15.4278C11.1521 15.4278 10.5156 15.9706 10.5156 16.6996C10.5156 17.4286 11.1521 17.9404 11.9814 17.9404Z" fill="white"></path></svg>
                <p class="thumnail_notice_text">사진을 첨부하지 않을 시, 기본 사진이 들어갑니다.</p>
            </div>
            <div class="nxt_btn_area">
                <button class="nxt_btn" id="nextButton4" style="display: block;">일정 생성하러가기</button>
            </div>
        </div>
    </div>`      
    //돔구현끝
    let personalExist = false;
    modalOpenButton.on('click', function() {
        modal.removeClass('hidden');
        modal.html(modalContent1);
    });
    
    //모달 닫는버튼
    modal.on('click', '#modalCloseButton', function() {
        $('#countrycode').val('');
        $('#regionname').val('');
        $('#startdate').val('');
        $('#enddate').val('');
        $('#package_title').val('');
        $('#package_content').val('');
        $('#maxcnt').val('');
        $('#adult_price').val('');
        $('#child_price').val('');
        modal.empty().addClass('hidden');            
    });
    //모달창 첫번째 분기
    modal.on('click', '.country', function() {
        // 선택된 버튼에 따라 modalContent 안의 DOM 변경
        if ($(this).hasClass("domestic")) {
            $('#countrycode').val('kr');
            modal.html(modalContent2);
        } else if ($(this).hasClass("overseas")) {
            modal.html(modalContentOverseas);
        }
    });

    
    modal.on('click', '.overseas', function() {
	    const region = $(this).data('country');
	    console.log(region);
	    selectedRegion = region; // 선택된 지역 정보를 전역 변수에 저장
	    $('#countrycode').val(region);
    	modal.html(modalContentOverSeaRegion[region]);	
    
	});

   
    modal.on('click', '.region', function() {
        // 다음 단계 모달 콘텐츠로 변경 (날짜 선택 등)
        modal.html(modalContent3);
    });
    modal.on('click', '.overregion', function() {
        const region = $(this).data('overregion');
	    console.log(region);
	    $('#regionname').val(region);
        modal.html(modalContentovercal);
    });
	let selectedRegion;
    //돌아가기 나누기
    modal.on('click', '.backButton', function(){
        const btnId = $(this).attr('id');
        
        if(btnId === 'c1'){
            $('#countrycode').val('');
            modal.html(modalContent1);
        }else if(btnId === 'c2'){
            $('#regionname').val('');
            modal.html(modalContent2);
        }else if(btnId === 'cor'){
			 $('#countrycode').val('');
            modal.html(modalContentOverseas);    
        } else if(btnId === 'covercal'){		
        	$('#countrycode').val('');
        	modal.html(modalContentOverSeaRegion[selectedRegion]); 
        }else if(btnId === 'c3'){
            $('#startdate').val('');
            $('#enddate').val('');
            modal.html(modalContent3);
        }else if(btnId === 'c4'){
            modal.html(modalContent4);
            package_title = $('#package_title');
            package_content = $('#package_content');
            $('#pac_title').val(package_title.val())
            $('#pac_content').val(package_content.val());
            package_title.val('');
            package_content.val('')
        }else if(btnId === 'c5'){
            personalExist = false;
            modal.html(modalContent5);
            adult_price = $('#adult_price');
            child_price = $('#child_price');
            maxcnt = $('#maxcnt');
            $('#a_price').val(adult_price.val());
            $('#c_price').val(child_price.val());
            maxcnt.val('');
            adult_price.val('');
            child_price.val('');
        }
    })

    //두번째 분기
    modal.on('click', '.region', function() {
        $('#regionname').val($(this).text());
        modal.html(modalContent3);
    });

   function isEndDateBeforeStartDate(start, end) {
	    const startDate = new Date(start);
	    const endDate = new Date(end);
	    return startDate > endDate;
	}

	modal.on('click', '#nextButton', function(){
	    const startDate = $('#calendar-startdate').val();
	    const endDate = $('#calendar-enddate').val();
	    
	    if (isEndDateBeforeStartDate(startDate, endDate)) {
	        $('.notice').addClass('on');
	        return;
	    }
	
	    $('#startdate').val(startDate);
	    $('#enddate').val(endDate);
	    modal.html(modalContent4);
	});

    
    // 제목, 내용 통과 조건
    function checkNextButton2() {
        const title = $('#pac_title').val();
        const content = $('#pac_content').val();
        if (title.trim().length >= 5 && title.trim().length <= 20 &&
            content.trim().length >= 10 && content.trim().length <= 100) {
            $('#nextButton2').addClass('on');
        } else {
            $('#nextButton2').removeClass('on');
        }
    }

    // 제목과 내용 입력 상태에 따라 다음 버튼 활성화
    modal.on('input', '#pac_title, #pac_content', function() {
      const titleLength = $('#pac_title').val().trim().length;
      const contentLength = $('#pac_content').val().trim().length;

      // 글자 수 업데이트
      $('.title_length').text(titleLength);
      $('.content_length').text(contentLength);
        checkNextButton2();
    });

   	modal.on('click',  '.calendar-dateInput', function(){
        $activeInput = $(this);
        const rect = this.getBoundingClientRect();
        $calendar.css({
            top: `${rect.bottom + window.scrollY}px`,
            left: `${rect.left + window.scrollX}px`,
            display: 'block'
        });
        buildCalendar(today.getFullYear(), today.getMonth());

        // Scroll to the current month
        const $currentMonthElement = $calendar.find('.calendar-month').eq(0);
        if ($currentMonthElement.length) {
            /*$currentMonthElement[0].scrollIntoView({ behavior: 'smooth' });*/
        }
        
        $(document).on('click.calendar', function(event) {
	        const $target = $(event.target);
	        if (!$target.closest('.calendar-container').length && !$target.hasClass('calendar-dateInput')) {
	            $calendar.hide();
	            $(document).off('click.calendar'); // 이벤트 핸들러를 제거합니다.
	        }
	    });
    });

    //넥스트 버튼 2눌렀을때
    modal.on('click', '#nextButton2', function(){
        const title = $('#pac_title').val();
        const content = $('#pac_content').val();
        $('#package_title').val(title);
        $('#package_content').val(content);
        modal.html(modalContent5);
        personalExist = false;
    });

    //max인원 select창 인원설정

    modal.on('focus', '#personal', function(){
        if(!personalExist){
            personalExist = true;
            for(let i = 1; i <= 30; i++){
                const personalOption = $('<option>',{
                    value: i,
                    text: i
                })
                $('#personal').append(personalOption);
            }
        }
    })

    //가격 받는부분 유효성
    modal.on('keydown', '#a_price, #c_price', function(event) {
        // 백스페이스, 삭제, 탭, ESC, 엔터, 쉼표만 허용
        if ($.inArray(event.keyCode, [46, 8, 9, 27, 13, 188]) !== -1 ||
            // Ctrl+A, Command+A 허용
            (event.keyCode === 65 && (event.ctrlKey === true || event.metaKey === true)) ||
            // Ctrl+C, Command+C 허용
            (event.keyCode === 67 && (event.ctrlKey === true || event.metaKey === true)) ||
            // Ctrl+V, Command+V 허용
            (event.keyCode === 86 && (event.ctrlKey === true || event.metaKey === true)) ||
            // Ctrl+X, Command+X 허용
            (event.keyCode === 88 && (event.ctrlKey === true || event.metaKey === true)) ||
            // home, end, left, right, down, up 허용
            (event.keyCode >= 35 && event.keyCode <= 40)) {
            // 허용되는 키라면 기본 이벤트 발생시키지 않음
            return;
        }
        // 숫자만 입력되도록 제어하고 키 이벤트 중단
        if ((event.shiftKey || (event.keyCode < 48 || event.keyCode > 57)) && (event.keyCode < 96 || event.keyCode > 105)) {
            event.preventDefault();
        }
    });

    // 가격 받는거 유효성검사 
    modal.on('keyup', '#a_price, #c_price', function() {
    let value = $(this).val().replace(/[^0-9]/g, ''); // 숫자 이외의 문자 제거
    value = Number(value);
    if (isNaN(value)) {
        $(this).val(0);
    } else {
        const formatValue = value.toLocaleString('ko-KR') + '원';
        $(this).val(formatValue);
    }
        checkValues();
    });

    chkPrice = false;
    //가격확인
    function checkValues() {
        const adultPriceValue = Number($('#a_price').val().replace(/[^0-9]/g, ''));
        const childPriceValue = Number($('#c_price').val().replace(/[^0-9]/g, ''));
        if (adultPriceValue > 2100000000 || childPriceValue > 2100000000) {
            $('.warning_text').addClass('on');
            $('#nextButton3').removeClass('on');
        } else {
            $('.warning_text').removeClass('on');
            $('#nextButton3').addClass('on');
        }
    }

    //다음 버튼3 활성화
    modal.on('keyup', '#personal, #a_price, #c_price', function() {
        const personalValue = $('#personal').val();
        const adultPriceValue = $('#a_price').val();
        const childPriceValue = $('#c_price').val();

        if (personalValue && adultPriceValue && childPriceValue) {
            checkValues();
        } else {
            $('#nextButton3').removeClass('on');
        }
    });
    //넥스트 버튼 3눌렀을때
    modal.on('click', '#nextButton3', function(){
        const maxcnt = $('#personal').val();
        const adult_price = $('#a_price').val().replace(/,/g, '').replace('원', '');
        const child_price = $('#c_price').val().replace(/,/g, '').replace('원', '');
        $('#maxcnt').val(maxcnt);
        $('#adult_price').val(adult_price);
        $('#child_price').val(child_price);
        modal.html(modalContent6);
    });
    //파일 처리부분
    modal.on('click', '.thumnail_area', function(e){
        e.stopPropagation();
        package_file.click();
    })
    $('#package_file').change(function(e){
        const fileTag = e.target;
        const file = fileTag.files[0];
        console.log(file);
        if(file != undefined){
            const reader = new FileReader();
            reader.onload = function(ie){
                $('.thumnail_img').attr('src', ie.target.result);
                $('.thumnail').addClass('hidden');
                $('.cancelFile').addClass('on');
            }
            reader.readAsDataURL(file);
        }
    })
    modal.on('click', '.cancelFile', function(e){
        e.stopPropagation();
        const fileInput = $('#package_file');
        fileInput.val(null);
        $('.thumnail').removeClass('hidden')
        $('.thumnail_img').attr('src', '/images/packageimg/tripfy_banner.png');
        $('.cancelFile').removeClass('on');
    })
    
    //modal 제출
    modal.on('click', '#nextButton4', function(){
        $("#packageForm").submit();
    });
    
     function buildCalendar(year, month) {
        let calendarHtml = '';
        const endYear = year + 1;

        while (year < endYear || (year === endYear && month < today.getMonth() + 7)) {
            const firstDay = new Date(year, month).getDay();
            const lastDate = new Date(year, month + 1, 0).getDate();

            calendarHtml += `<div class="calendar-month"><table>`;
            calendarHtml += `<caption>${year}년 ${month + 1}월</caption>`;
            calendarHtml += '<tr>';

            const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];
            $.each(daysOfWeek, function(index, day) {
                calendarHtml += `<th>${day}</th>`;
            });

            calendarHtml += '</tr><tr>';

            for (let i = 0; i < firstDay; i++) {
                calendarHtml += '<td></td>';
            }

            for (let date = 1; date <= lastDate; date++) {
                const currentDate = new Date(year, month, date);
                const isToday = currentDate.toDateString() === today.toDateString();
                const isPastDate = currentDate < today;
                const className = isToday ? 'calendar-current-day' : (isPastDate ? 'calendar-past-day' : '');
                if ((firstDay + date - 1) % 7 === 0 && date !== 1) {
                    calendarHtml += '</tr><tr>';
                }

                calendarHtml += `<td class="${className}" data-date="${year}-${String(month + 1).padStart(2, '0')}-${String(date).padStart(2, '0')}">${date}</td>`;
            }

            calendarHtml += '</tr></table></div>';

            if (month === 11) {
                year++;
                month = 0;
            } else {
                month++;
            }
        }

        $calendar.html(calendarHtml);
        $('.calendar-past-day').text("");
        $calendar.find('td[data-date]').on('click', function() {
		    if (!$(this).hasClass('calendar-past-day')) {
		        $activeInput.val($(this).data('date'));
		        $calendar.hide();
		    }
		});

    }
});