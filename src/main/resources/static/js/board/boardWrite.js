let mapModal = $("#modal");
let geocoder = new google.maps.Geocoder();
let map;
let span = $(".close");
let marker = null; // 단일 마커를 저장할 변수
	$(document).ready(function(){
		//썸머노트 설정값
	    $('#summernote').summernote({        
	        codeviewFilter: false, // 코드 보기 필터 비활성화
	            codeviewIframeFilter: false, // 코드 보기 iframe 필터 비활성화
	            lang: 'ko-KR',
	            placeholder: '장소의 사진이나 메모를 작성하세요',
	            width: 950,
	            maxWidth: 950,
	            height: 500,
	            maxHeight: 1000,
	            focus : true,
	            toolbar: [
	                ['style', ['style']], // 글자 스타일 설정 옵션
	                ['fontsize', ['fontsize']], // 글꼴 크기 설정 옵션
	                ['fontname', ['fontname']],
	                ['font', ['bold', 'underline', 'clear']], // 글자 굵게, 밑줄, 포맷 제거 옵션
	                ['color', ['color']], // 글자 색상 설정 옵션
	                ['table', ['table']], // 테이블 삽입 옵션
	                ['para', ['ul', 'ol', 'paragraph']], // 문단 스타일, 순서 없는 목록, 순서 있는 목록 옵션
	                ['height', ['height']], // 에디터 높이 조절 옵션
	                ['insert', ['picture', 'link', 'video']], // 이미지 삽입, 링크 삽입, 동영상 삽입 옵션
	                ['view', ['codeview', 'fullscreen', 'help']], // 코드 보기, 전체 화면, 도움말 옵션
	            ],
	            fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','굴림','돋움체','바탕체','Nanum Gothic'],
	            fontSizes: [
	                '8', '9', '10', '11', '12', '14', '16', '18',
	                '20', '22', '24', '28', '30', '36', '50', '72',
	            ], // 글꼴 크기 옵션
	            styleTags: [
	                'p',  // 일반 문단 스타일 옵션
	                {
	                    title: 'Blockquote',
	                    tag: 'blockquote',
	                    className: 'blockquote',
	                    value: 'blockquote',
	                },  // 인용구 스타일 옵션
	                'pre',  // 코드 단락 스타일 옵션
	                {
	                    title: 'code_light',
	                    tag: 'pre',
	                    className: 'code_light',
	                    value: 'pre',
	                },  // 밝은 코드 스타일 옵션
	                {
	                    title: 'code_dark',
	                    tag: 'pre',
	                    className: 'code_dark',
	                    value: 'pre',
	                },  // 어두운 코드 스타일 옵션
	                'h1', 'h2', 'h3', 'h4', 'h5', 'h6',  // 제목 스타일 옵션
	            ],
	         callbacks: {
	            onImageUpload: function (files) {
	               uploadSummernoteImageFile(files[0],this);
	            },
	            onMediaDelete: function ($target, editor, $editable) {
	                if (confirm('이미지를 삭제 하시겠습니까?')) {
	                    var deletedImageUrl = $target
	                        .attr('src')
	                        .split('/')
	                        .pop()
	                    // ajax 함수 호출
	                    deleteSummernoteImageFile(deletedImageUrl)
	                }
	            },
	        },
	    });

		$(document).on("click","#modalOnBtn", function(){
			mapModal.show();
			 if (!map) {
	            geocodeLocation(defaultLocationName);
	        } else {
	            google.maps.event.trigger(map, 'resize');
	            map.setCenter(map.getCenter());
	        }
	
	        span.on("click", function() {
	            resetMapAndControls();
	            mapModal.hide();
	        });
	    
	        $(window).on("click", function(event) {
	            if ($(event.target).is(mapModal)) {
	                resetMapAndControls();
	                mapModal.hide();
	            }
	        });
		});
		
		$('.place_select_btn').on("click", function(){
	        $('.b_place_detail').val($('#description').text());
	        resetMapAndControls();
	        mapModal.hide();
	        
	        $('#description').text();
	        $('.place_select_btn').removeClass('on');
	    })			
	})
	
	function geocodeLocation(locationName) {
       geocoder.geocode({ 'address': locationName }, function(results, status) {
           if (status === 'OK') {
               if (!map) {
                   initMap(results[0].geometry.location);
               } else {
                   map.setCenter(results[0].geometry.location);
               }
           } else {
               alert('Geocode was not successful for the following reason: ' + status);
           }
       });
   }
   
   function initMap(location) {
        map = new google.maps.Map(document.getElementById('map'), {
            center: location,
            zoom: 11
        });

        input = document.getElementById('pac-input');
        searchBox = new google.maps.places.SearchBox(input);

        map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

        map.addListener('bounds_changed', function() {
            searchBox.setBounds(map.getBounds());
        });

        searchBox.addListener('places_changed', function() {
            var places = searchBox.getPlaces();

            if (places.length == 0) {
                return;
            }

            // 기존 마커 제거
            if (marker) {
                marker.setMap(null);
            }

            var bounds = new google.maps.LatLngBounds();
            var place = places[0]; // 첫 번째 장소만 사용

            if (!place.geometry) {
                return;
            }

            // 새로운 마커 생성 및 설정
            marker = new google.maps.Marker({
                map: map,
                title: place.name,
                position: place.geometry.location
            });

            $('#description').text(place.formatted_address);
            if($('#description').text() !== ""){
                $('.place_select_btn').addClass('on');
            }else{
                $('.place_select_btn').removeClass('on');
            }

            if (place.geometry.viewport) {
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }

            map.fitBounds(bounds);
        });

        // 클릭 이벤트 추가
        map.addListener('click', function(event) {
            // 새로운 클릭 지점의 위도와 경도
            var clickedLocation = event.latLng;
            
            // 기존 마커 제거
            if (marker) {
                marker.setMap(null);
            }

            // 새로운 마커 생성
            marker = new google.maps.Marker({
                position: clickedLocation,
                map: map
            });

            // 마커 위치를 주소로 변환하여 표시
            geocoder.geocode({ 'location': clickedLocation }, function(results, status) {
                if (status === 'OK') {
                    if (results[0]) {
                        $('#description').text(results[0].formatted_address);
                        $('.place_select_btn').addClass('on');
                    } else {
                        window.alert('No results found');
                    }
                } else {
                    window.alert('Geocoder failed due to: ' + status);
                }
            });
        });
    }
    
    function resetMapAndControls() {
        if (input) {
            input.value = '';
        }
        $('#description').text('');

        // 기존 마커 제거
        if (marker) {
            marker.setMap(null);
            marker = null;
        }

        if (map && searchBox) {
            searchBox.setBounds(map.getBounds());
            google.maps.event.trigger(map, 'resize');
            map.setCenter(map.getCenter());
        }
        geocodeLocation(defaultLocationName);
    }

	function uploadSummernoteImageFile(file, editor) {
	    let data = new FormData();
		data.append("file", file);
		$.ajax({
			data : data,
			type : "POST",
			url : "/board/SummerNoteImageFile",
			contentType : false,
			processData : false,
			success : function(result){
				$(editor).summernote("insertImage",result);
			},
			error: function(){
				console.log("파일 올리기 오류");
			}
		});
	}
	
	function deleteSummernoteImageFile(imageName) {
	    data = new FormData()
	    data.append('file', imageName)
	    $.ajax({
	        data: data,
	        type: 'POST',
	        url: '/board/deleteSummernoteImageFile',
	        contentType: false,
	        enctype: 'multipart/form-data',
	        processData: false,
	    })
	}