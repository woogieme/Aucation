"use client";

import { useEffect, useState } from "react";

declare global {
  interface Window {
    kakao: any;
  }
}
interface KakaoMapProp {
  inputLat: number;
  inputLag: number;
}
const StayMap: React.FC<KakaoMapProp> = ({ inputLat, inputLag }) => {
  const [lat, setLat] = useState(0);
  const [lng, setLng] = useState(0);
  const [markerXY, setMarker] = useState<number[] | null>(null);

  useEffect(() => {
    console.log("1111111111111111", inputLat);
    setLat(inputLat);
    setLng(inputLag);
  }, []);

  useEffect(() => {
    const kakaoMapScript = document.createElement("script");
    kakaoMapScript.async = false;
    kakaoMapScript.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=${process.env.NEXT_PUBLIC_KAKAO_MAP_API_KEY}&libraries=services`;
    document.head.appendChild(kakaoMapScript);

    const onLoadKakaoAPI = () => {
      window.kakao.maps.load(() => {
        var container = document.getElementById("map");
        var options = {
          center: new window.kakao.maps.LatLng(lat, lng),
          level: 3,
        };

        var map = new window.kakao.maps.Map(container, options);
        var markerPosition = new window.kakao.maps.LatLng(lat, lng);
        var marker = new window.kakao.maps.Marker({
          position: markerPosition,
        });
        marker.setMap(map);
        var geocoder = new window.kakao.maps.services.Geocoder();
        var infowindow = new window.kakao.maps.InfoWindow({ zindex: 1 });

        // 초기 마커 위치 정보에 대한 주소 정보를 가져옵니다.
        searchDetailAddrFromCoords(markerPosition, function (result: any, status: any) {
          if (status === window.kakao.maps.services.Status.OK) {
            var detailAddr = !!result[0].road_address
              ? "<div>도로명주소 : " + result[0].road_address.address_name + "</div>"
              : "";
            detailAddr +=
              '<div className="break-all">지번 주소 : ' + result[0].address.address_name + "</div>";

            var content =
              '<div class="bAddr py-1 px-2 overflow-ellipsis overflow-hidden whitespace-nowrap block ">' +
              detailAddr +
              "</div>";

            // 마커를 클릭한 위치에 표시합니다
            marker.setPosition(markerPosition);
            marker.setMap(map);

            // 인포윈도우에 클릭한 위치에 대한 법정동 상세 주소정보를 표시합니다
            infowindow.setContent(content);
            infowindow.open(map, marker);
          }
        });

        // 중심 좌표나 확대 수준이 변경됐을 때 지도 중심 좌표에 대한 주소 정보를 표시하도록 이벤트를 등록합니다
        window.kakao.maps.event.addListener(map, "idle", function () {
          searchAddrFromCoords(map.getCenter(), displayCenterInfo);
        });

        function searchAddrFromCoords(coords: any, callback: any) {
          // 좌표로 행정동 주소 정보를 요청합니다
          geocoder.coord2RegionCode(coords.getLng(), coords.getLat(), callback);
        }

        function searchDetailAddrFromCoords(coords: any, callback: any) {
          // 좌표로 법정동 상세 주소 정보를 요청합니다
          geocoder.coord2Address(coords.getLng(), coords.getLat(), callback);
        }

        // 지도 좌측상단에 지도 중심좌표에 대한 주소정보를 표출하는 함수입니다
        function displayCenterInfo(result: any, status: any) {
          if (status === window.kakao.maps.services.Status.OK) {
            var infoDiv = document.getElementById("centerAddr");

            for (var i = 0; i < result.length; i++) {
              // 행정동의 region_type 값은 'H' 이므로
              if (result[i].region_type === "H") {
                infoDiv!.innerHTML = result[i].address_name;
                break;
              }
            }
          }
        }
        // marker.setDraggable(true);
      });
    };
    if (lat && lng) {
      kakaoMapScript.addEventListener("load", onLoadKakaoAPI);
    }
  }, [lat, lng]);

  return (
    <main className="w-full flex flex-col items-center justify-center">
      <div className="w-full h-[500px]">
        <div id="map" style={{ width: "100%", height: "100%" }} className="rounded-lg">
          {/* 왼쪽 위 어쩌고 */}
          <div className="flex w-[210px] border-2 bg-opacity-70 border-gray-300 ml-3 absolute z-40 px-1 py-3 top-3 rounded-2xl text-sm bg-white">
            <div>
              <p className="title font-bold ">지도중심기준 행정동 주소정보</p>
              <p id="centerAddr" className="font-light"></p>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
};
export default StayMap;
