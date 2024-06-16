let ppmContentMain = $('.ppm-content-main');

// 모달 열기
$(document).on('click', '#pacPrintOpen', function() {
    $('#pacPrintModalWrapper').removeClass('ppm-hidden');
});

// 모달 닫기
$(document).on('click', '.ppm-header-close', function() {
    $('#pacPrintModalWrapper').addClass('ppm-hidden');
});

// 인쇄 버튼 클릭 시
$(document).on('click', '#ppm-print-btn', function() {
    let printContents = ppmContentMain.html();
    $('body').children().not('#printArea').addClass('allElementHidden');
    $("#printArea").html(printContents);
    window.print();
    $("#printArea").html("");
    $('body').children().removeClass('allElementHidden');
});

//일정표 버튼
$(document).on('click', '#ppm-detailSummaryInclu', function(){
    $('#ppm-detailSummaryNotInclu').removeClass('ppmSidebarBtnOn');
    $(this).addClass('ppmSidebarBtnOn');
    $('.tlplaceDetail').removeClass('ppm-hidden');
})
$(document).on('click', '#ppm-detailSummaryNotInclu', function(){
    $('#ppm-detailSummaryInclu').removeClass('ppmSidebarBtnOn');
    $(this).addClass('ppmSidebarBtnOn');
    $('.tlplaceDetail').addClass('ppm-hidden');
})
//유의사항 버튼
let precautionsDiv = $('h3').filter(function() {
    return $(this).text() === '유의사항';
}).closest('div');
$(document).on('click', '#ppm-preInclu', function(){
    $('#ppm-preInNotInclu').removeClass('ppmSidebarBtnOn');
    $(this).addClass('ppmSidebarBtnOn');
    $('.ppm-precautions').removeClass('ppm-hidden');
    $(precautionsDiv).removeClass('ppm-hidden');
})
$(document).on('click', '#ppm-preInNotInclu', function(){
    $('#ppm-preInclu').removeClass('ppmSidebarBtnOn');
    $(this).addClass('ppmSidebarBtnOn');
    $('.ppm-precautions').addClass('ppm-hidden');
    $(precautionsDiv).addClass('ppm-hidden');
})
//결제약관 버튼
let touDiv = $('h3').filter(function() {
    return $(this).text() === '결제 약관';
}).closest('div');
$(document).on('click', '#ppm-paymentTermsInclu', function(){
    $('#ppm-paymentTermsNotInclu').removeClass('ppmSidebarBtnOn');
    $(this).addClass('ppmSidebarBtnOn');
    $('.ppm-detail-tou').removeClass('ppm-hidden');
    $(touDiv).removeClass('ppm-hidden');
})
$(document).on('click', '#ppm-paymentTermsNotInclu', function(){
    $('#ppm-paymentTermsInclu').removeClass('ppmSidebarBtnOn');
    $(this).addClass('ppmSidebarBtnOn');
    $('.ppm-detail-tou').addClass('ppm-hidden');
    $(touDiv).addClass('ppm-hidden');
})