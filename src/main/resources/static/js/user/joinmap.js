const addr = document.getElementById("addr");
const inputplaceid = document.getElementById("inputplaceid");
/* google placeid api 
https://developers.google.com/maps/documentation/javascript/examples/places-placeid-finder?_gl=1*1y6i2bk*_up*MQ..*_ga*MTA1NzU1NDA4My4xNzE1ODIxNTQz*_ga_NRWSTWS78N*MTcxNTgyMTU0My4xLjAuMTcxNTgyMTU3Ni4wLjAuMA..#maps_places_placeid_finder-javascript
*/
function initMap() {
  const map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: -33.8688, lng: 151.2195 },
    zoom: 16,
  });
  const input = document.getElementById("pac-input");
  // Specify just the place data fields that you need.
  const autocomplete = new google.maps.places.Autocomplete(input, {
    fields: ["place_id", "geometry", "formatted_address", "name"],
  });

  autocomplete.bindTo("bounds", map);
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  const infowindow = new google.maps.InfoWindow();
  const infowindowContent = document.getElementById("infowindow-content");

  infowindow.setContent(infowindowContent);

  const marker = new google.maps.Marker({ map: map });

  marker.addListener("click", () => {
    infowindow.open(map, marker);
  });
  autocomplete.addListener("place_changed", () => {
    infowindow.close();

    const place = autocomplete.getPlace();

    if (!place.geometry || !place.geometry.location) {
      return;
    }

    if (place.geometry.viewport) {
      map.fitBounds(place.geometry.viewport);
    } else {
      map.setCenter(place.geometry.location);
      map.setZoom(17);
    }

    // Set the position of the marker using the place ID and location.
    // @ts-ignore This should be in @typings/googlemaps.
    marker.setPlace({
      placeId: place.place_id,
      location: place.geometry.location,
    });
    marker.setVisible(true);
    console.log(place.name);
    console.log(place.place_id);
    console.log(place.formatted_address);
    infowindowContent.children.namedItem("place-name").textContent = "주소";
    infowindowContent.children.namedItem("place-id").textContent = place.place_id;
    infowindowContent.children.namedItem("place-address").textContent =
      place.formatted_address;
    infowindow.open(map, marker);
    
    addr.setAttribute("value", place.formatted_address);
    inputplaceid.setAttribute("value", place.place_id);
  });
}

window.initMap = initMap;