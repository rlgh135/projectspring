const result = document.getElementById("result");
//비밀번호 유효성 검사를 위한 배열
let pwTest = [false,false]

function sendit(){
    const joinForm = document.joinForm;

    const userid = joinForm.userid;
    if(userid.value == ""){
    	alert("아이디를 입력하세요!");
    	userid.focus();
    	return;
    }
    if(userid.value.length<5 || userid.value.length>12){
    	alert("아이디는 5자 이상 12자 이하로 입력하세요!");
    	userid.focus();
    	return;
    }
    
    if(result.innerHTML == ""){
    	alert("아이디 중복검사를 진행해주세요!");
    	userid.focus();
    	return;
    }
    if(result.innerHTML == "중복된 아이디가 있습니다!"){
    	alert("중복체크 통과 후 가입이 가능합니다!");
    	userid.focus();
    	return;
    }
    
    //아래쪽의 pwcheck() 함수를 통해 유효성 검사를 통과했다면 pwTest 배열에는 true값만 존재한다.
    //무언가 실패했다면 false가 포함되어 있으므로, 반복문을 통해 해당 배열을 보며 false값이 있는지 검사
    for(let i=0;i<2;i++){
    	if(!pwTest[i]){
    		alert("비밀번호 확인을 다시하세요!");
    		userpw.focus();
    		return;
    	}
    }
    
    const usergender = joinForm.gender;
    if(!usergender[0].checked && !usergender[1].checked){
    	alert("성별을 선택하세요!");
    	return;
    }
    const foreigner = joinForm.foreigner;
    if(!foreigner[0].checked && !foreigner[1].checked){
    	alert("국적을 선택하세요!");
    	return;
    }
    
    const addr = joinForm.addr;
    if(addr.value == ""){
		alert("주소를 선택하세요!");
		return;
	}
    joinForm.submit();
}
function pwcheck(){
    const userpw = document.joinForm.userpw;
    const userpw_re = document.joinForm.userpw_re;
    //아래쪽에 있는 귀여운 span 태그들 가져오기
    const pw_result = document.getElementById("pw_result");
    const pwre_result = document.getElementById("pwre_result");
    //영어 대문자, 영어 소문자, 숫자, 특수문자를 한 글자씩 포함하는지 확인하는 정규식
    const reg = /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[~?!@-]).{4,}$/;
    
    if(userpw.value != ""){
		if(reg.test(userpw.value)){
			if(userpw.value.length>7){
				if(/^[a-zA-Z0-9~?!@-]*$/.test(userpw.value)){
					pw_result.innerHTML = "<span style='color: rgb(0,200,80);'>안전한 비밀번호에요</span>";
    				pwTest[0] = true;
				} else {
					pw_result.innerHTML = "비밀번호는 8글자 이상, 대문자, 특수문자가 반드시 들어가야 합니다.";
			    	pwTest[0] = false;
				}
			} else {
				pw_result.innerHTML = "비밀번호는 8글자 이상, 대문자, 특수문자가 반드시 들어가야 합니다.";
		    	pwTest[0] = false;
			}
		} else {
			pw_result.innerHTML = "비밀번호는 8글자 이상, 대문자, 특수문자가 반드시 들어가야 합니다.";
	    	pwTest[0] = false;
		}
    } else {
		pw_result.innerHTML = "비밀번호는 8글자 이상, 대문자, 특수문자가 반드시 들어가야 합니다.";
    	pwTest[0] = false;
	}
    
    if(userpw.value != userpw_re.value){
    	pwre_result.innerHTML = "비밀번호를 확인해주세요";
    	pwTest[1] = false;
    }
    else{
    	pwre_result.innerHTML = "<span style='color: rgb(0,200,80);'>비밀번호가 일치해요</span>";
    	pwTest[1] = true;
    }
}
  
function checkId(){
	const xhr = new XMLHttpRequest();
	const userid = document.joinForm.userid;
	if(userid.value == ""){
		alert("아이디를 입력하세요!");
		userid.focus();
		return;
	}
	
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4){
			if(xhr.status == 200){
				let txt = xhr.responseText.trim();
				if(txt == "O"){
					result.innerHTML = "<span style='color: rgb(0,200,80);'>사용할 수 있는 아이디입니다</span>";
					document.joinForm.userpw.focus();
				}
				else{
					result.innerHTML = "중복된 아이디가 있습니다!";
					userid.value = "";
					userid.focus();
				}
			}
		}
	}
	
	xhr.open("GET","/user/checkId?userid="+userid.value);
	xhr.send();
}














