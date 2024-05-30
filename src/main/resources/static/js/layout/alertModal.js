const alertModal = $("#myModal");
const alertClostBtn = $('.alertModalClose');
function showAlertModal(imgNum,text){
    $(".alertModal h3").text(text);
    let imgSrc = '<img src="/images/alertImg/1-'+imgNum+'.png" alt="">';
    $(".alertModal_content div:first-child").html(imgSrc);

    alertModal.show();
    alertClostBtn.click(function() {
        alertModal.hide();
        $(".modal h3").text("");
    });

    $(window).click(function(event) {
        if (event.target.id == 'myModal') {
            alertModal.hide();
            $(".modal h3").text("");
        }
    });
}