$(document).ready(function() {
	const dayLocations = {};
	let geocoder = new google.maps.Geocoder();
	let body_markers = [];
	let polyline = null;
	let contentModal = $("#contentModal");
	let map;
	let input;
	let searchBox;
	let marker = null; // 단일 마커를 저장할 변수
	const defaultLocationName = $('.region_name span').text(); //리전네임 받아서 하는게 안전
	let body_map;	
	
    for(i=1;i<=days;i++){
	    showDayList(packagenum,i);		
	}
  
  	$(document).on("click", "#package_modify", function(){
		window.location.href = '/package/tlwrite?packagenum=' + packagenum;
	})
	
	$(document).on("click", ".getContent_btn", function(){
		let dayDetailBoxId = $(this).closest('.day_detail_box').attr('id');
	    let thisDay = dayDetailBoxId.split('-')[0];
	    let thisDetailNum = dayDetailBoxId.split('-')[1];
	    let placeNameText = $(this).closest('.day_detail_box').find('.place_name').text();
        contentModal.show();
        $(".McDetail_place p").text(placeNameText);
        $("this_day coqSbM").text("Day"+thisDay);
        $(".McDetail_num p span").text(thisDetailNum);
        timelineService.getContent(
			{"packagenum":packagenum,"day":thisDay,"detailNum":thisDetailNum},
			function(result){
				if(result == null){
					$("#timeline_contents").html("<p>등록된 상세설명이 없습니다</p>");
				}
				$("#timeline_contents").html(result.contents);
			}
		)
    })
    
    $(document).on("click", "#cancelButton", function(){
        contentModal.hide();
    })
    
    //마우스 올릴때 위치 이동
    $(document).on("mouseenter", ".day_detail_box", function() {
        thisDay = $(this).attr("id").split("-")[0];
        thisPlace = $(this).attr("id").split("-")[1];
        this_Place_Distance = dayLocations[thisDay][thisPlace-1];
        latitude = this_Place_Distance.lat;
        longitude = this_Place_Distance.lng;
        showDay(thisDay);
        centerLatLng = new google.maps.LatLng(latitude, longitude);
        body_map.setCenter(centerLatLng);
    });

    //삭제구현
    $(document).on("click", ".delete_btn", function(){
	   let dayDetailBoxId = $(this).closest('.day_detail_box').attr('id');
	   let thisDay = dayDetailBoxId.split('-')[0];
	   let thisDetailNum = dayDetailBoxId.split('-')[1];
	   console.log(packagenum+"+"+thisDay+"+"+thisDetailNum);
       timelineService.delete(
			{"packagenum":packagenum,"day":thisDay,"detailNum":thisDetailNum},
			function(result){
				console.log(result);
				showDayList(packagenum, thisDay);
			}
		)
    })

    geocodeAndInitMap(defaultLocationName);
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
   
    function geocodeAndInitMap(locationName) {
        geocoder.geocode({ 'address': locationName }, function(results, status) {
            if (status === 'OK') {
                if (!body_map) {
                    body_initMap(results[0].geometry.location);
                } else {
                    body_map.setCenter(results[0].geometry.location);
                }
            } else {
                alert('Geocode was not successful for the following reason: ' + status);
            }
        });
    }

    function body_initMap(centerLocation) {
        body_map = new google.maps.Map(document.getElementById("body_map"), {
            center: centerLocation,
            zoom: 11,
        });
    }

    for (let i = 1; i <= days; i++) {
        $("#day" + i).mouseenter(function() {
            showDay(i);
        });
        $(".day" + i).mouseenter(function() {
            showDay(i);
        });
    }

	async function showDayList(packagenum, day) {
    dayLocations[day] = [];
    $("#day" + day + "-list").empty();
    timelineService.get(
        {"packagenum": packagenum, "day": day},
        async function(result) {
            if (result == null) {
                return;
            }
            result.sort((a, b) => a.detailNum - b.detailNum);
            for (let i = 0; i < result.length; i++) {
                const item = result[i];
                geocodeLocationAndAddToDay(item.day, item.title);
                let add_place_dom = generatePlaceDOM(item.title, item.day, item.detailNum);
                if (item.detailNum == 1) {
                    $("#day" + day + "-list").append(add_place_dom);
                } else {
                    const previousItem = result[i - 1];
                    try {
                        const distance = await getDistanceAsync(item.title, previousItem.title);
                        console.log(distance);
                        let add_place_between = generatePlaceBetweenDOM(distance.toFixed(2));
                        $("#day" + day + "-list").append(add_place_between);
                        $("#day" + day + "-list").append(add_place_dom);
                    } catch (error) {
                        console.error(error);
                    }
                }
            };
        }
    )
}
	
	//사이 선 dom
	function generatePlaceBetweenDOM(distance) {
	    return `<div class="detail_between">
		    <div>
		        <div></div>
		        <div></div>
		        <div></div>
		    </div>
		    <div>
		        <span>${distance}km</span>
		    </div>
		</div>`;
	}
	
	//장소 추가 했을 시 dom
	function generatePlaceDOM(pn, d, ln) { 
	    let pnum = 0;
	    if(ln <= 3){
	        pnum = ln;
	    }else{
	        pnum = ((ln - 1) % 3) + 1;
	    }
	     return `<div class="day_detail_box" id="${d}-${ln}">
	        <div class="day_detail">
	            <div class="detail_num">
	                <div class="p${pnum}">${ln}</div>
	            </div>
	            <div class="detail_place">
	                <div class="place_name">${pn}</div>
	                <div class="btn_area">
	                    <div class="getContent_btn">
	                       <svg style ="cursor:pointer;" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
<circle cx="12" cy="5" r="2.25" transform="rotate(90 12 5)" stroke="#1F2023" stroke-width="1.5" stroke-linecap="round"/>
<circle cx="12" cy="12" r="2.25" transform="rotate(90 12 12)" stroke="#1F2023" stroke-width="1.5" stroke-linecap="round"/>
<circle cx="12" cy="19" r="2.25" transform="rotate(90 12 19)" stroke="#1F2023" stroke-width="1.5" stroke-linecap="round"/>
</svg>

	                    </div>
	                </div>
	            </div>
	        </div>
	    </div>`;
	}
	
	function getDistanceAsync(place1, place2) {
    return new Promise((resolve, reject) => {
        var geocoder = new google.maps.Geocoder();
        
        geocoder.geocode({ 'address': place1 }, function(results, status) {
            if (status === 'OK') {
                var location1 = results[0].geometry.location;
                
                geocoder.geocode({ 'address': place2 }, function(results, status) {
                    if (status === 'OK') {
                        var location2 = results[0].geometry.location;
                        
                        var distance = google.maps.geometry.spherical.computeDistanceBetween(location1, location2) / 1000; // km
                        resolve(distance);
                    } else {
                        console.error('Geocode was not successful for the following reason: ' + status);
                        reject(new Error('Geocode failed for place2'));
                    }
                });
            } else {
                console.error('Geocode was not successful for the following reason: ' + status);
                reject(new Error('Geocode failed for place1'));
            }
        });
    });
}

	// day와 지역 이름을 받아 좌표를 저장하는 함수
	function geocodeLocationAndAddToDay(day, locationName) {
	    geocoder.geocode({ 'address': locationName }, function(results, status) {
	        if (status === 'OK') {
	            const location = {
	                lat: results[0].geometry.location.lat(),
	                lng: results[0].geometry.location.lng()
	            };
	            addLocationToDay(day, location);
	            showDay(day);
	        } else {
	            alert('Geocode was not successful for the following reason: ' + status);
	        }
	    });
	}
	
	function addLocationToDay(day, location) {
	    if (!dayLocations[day]) {
	        dayLocations[day] = [];
	    }
	    dayLocations[day].push(location);
	}   
	
	function showDay(day) {
	    clearMarkers();
	    // 예시 데이터
	    const locations = dayLocations[day] || [];
	    locations.forEach((location, index) => {
	        const marker = new google.maps.Marker({
	            position: location,
	            map: body_map,
	            icon:{
	                url: customMarkerImages[index + 1].url,
	                scaledSize: customMarkerImages[index + 1].scaledSize,
	                origin: customMarkerImages[index + 1].origin,
	                anchor: customMarkerImages[index + 1].anchor
	            }
	        });
	        body_markers.push(marker);
	    });
	
	
	    if (body_markers.length > 1) {
	        const lineCoordinates = body_markers.map(marker => marker.getPosition());
	        polyline = new google.maps.Polyline({
	            path: lineCoordinates,
	            geodesic: true,
	            strokeColor: "#1976D2",
	            strokeOpacity: 1.0,
	            strokeWeight: 2,
	        });
	        polyline.setMap(body_map);
	    }
	}
	
	function clearMarkers() {
	    body_markers.forEach(marker => marker.setMap(null));
	    body_markers = [];
	    if (polyline) {
	        polyline.setMap(null);
	        polyline = null;
	    }
	}
});

const timelineService = {
		getContent:function(data, callback){
			$.ajax({
				type:"GET",
				url:"/package/getTimelineContent",
				data:$.param(data),
				contentType:"application/json;charset=utf-8",
				success:function(result){
					callback(result)
				},
				error:function(status,xhr){
					 console.error("AJAX Error:", xhr.status, status);
				}
			})
		},
		get:function(data, callback){
			$.ajax({
				type:"GET",
				url:"/package/timelineList",
				data:$.param(data),
				contentType:"application/json;charset=utf-8",
				success:function(result){
					callback(result)
				},
				error:function(status,xhr){
					 console.error("AJAX Error:", xhr.status, status);
				}
			})
		},
		getContent:function(data, callback){
			$.ajax({
				type:"GET",
				url:"/package/getTimelineContent",
				data:$.param(data),
				contentType:"application/json;charset=utf-8",
				success:function(result){
					callback(result)
				},
				error:function(status,xhr){
					 console.error("AJAX Error:", xhr.status, status);
				}
			})
		},
	}
