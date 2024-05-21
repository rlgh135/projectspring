$(document).ready(function() {
    const modal = $('#modalContainer');
    const modalOpenButton = $('#modalOpenButton');
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
            <form autocomplete="off">
                <div class="flex-row d-flex justify-content-center">
                    <div class="col-lg-6 col-11 px-1">
                        <div class="input-group input-daterange">
                            <input type="text" id="start" class="form-control text-left mr-2">
                            <label class="ml-3 form-control-placeholder" id="start-p" for="start">시작</label>
                            <span class="fa fa-calendar" id="fa-1"></span>
                            <input type="text" id="end" class="form-control text-left ml-2">
                            <label class="ml-3 form-control-placeholder" id="end-p" for="end">종료</label>
                            <span class="fa fa-calendar" id="fa-2"></span>
                        </div>
                    </div>
                </div>
            </form>
            <div class="nxt_btn_area">
              <p class="notice"><svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="12" cy="12" r="10" fill="#1976D2"></circle><path d="M11.2485 14.0939H12.7336L13.0422 7.88978L13.1194 6.05957H10.8821L10.9399 7.88978L11.2485 14.0939ZM11.9814 17.9404C12.8301 17.9404 13.4858 17.4286 13.4858 16.6996C13.4858 15.9706 12.8301 15.4278 11.9814 15.4278C11.1521 15.4278 10.5156 15.9706 10.5156 16.6996C10.5156 17.4286 11.1521 17.9404 11.9814 17.9404Z" fill="white"></path></svg>시작일은 현재일 기준 8일 이후부터 가능합니다</p>
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
            modal.html("<h2>해외로 이동합니다.</h2>");
        }
    });
    //돌아가기 나누기
    modal.on('click', '.backButton', function(){
        const btnId = $(this).attr('id');
        if(btnId === 'c1'){
            $('#countrycode').val('');
            modal.html(modalContent1);
        }else if(btnId === 'c2'){
            $('#regionname').val('');
            modal.html(modalContent2);
        }else if(btnId === 'c3'){
            $('#startdate').val('');
            $('#enddate').val('');
            modal.html(modalContent3);
            datepicker();
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

    // yyyy-mm-dd 형식의 날짜 문자열을 받아서 해당 날짜가 오늘로부터 8일 이후인지 체크하는 함수
    function checkStartDate(startDate) {
        const today = new Date();
        today.setDate(today.getDate() + 8);
        const targetDate = new Date(startDate); 
        return targetDate > today;
    }

    //start,end 여부 확인
    function checkNextButton() {
        const startDate = $('#start').val();
        const endDate = $('#end').val();
        if (startDate.trim() !== '' && endDate.trim() !== '') {
            if(checkStartDate(startDate)){
              $('#nextButton').addClass('on');
              $('.notice').removeClass('on');
            }else{
              $('#nextButton').removeClass('on');
              $('.notice').addClass('on');
            }
        } else {
            $('#nextButton').removeClass('on');
        }
    }

    // 데이트피커 활성화
    function datepicker(){
      $('.input-daterange').datepicker({
            format: 'yyyy-mm-dd',
            autoclose: true,
            calendarWeeks : false,
            clearBtn: true,
            disableTouchKeyboard: true
        }).on('changeDate', function(){
            checkNextButton();
        });
    }
    //두번째 분기
    modal.on('click', '.region', function() {
        $('#regionname').val($(this).text());
        modal.html(modalContent3);
        datepicker();
    });

    //날짜 선택 후 다음누를때
    modal.on('click', '#nextButton', function(){
        const startDate = $('#start').val();
        const endDate = $('#end').val();
        $('#startdate').val(startDate);
        $('#enddate').val(endDate);
        modal.html(modalContent4);
    })
    
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
    $('[type=file]').change(function(e){
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
});