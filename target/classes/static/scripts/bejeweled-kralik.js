var positions = [];
var srcCoordinates = [];
var destCoordinates = [];
var rowsOfLastGeneratedTiles = [];
var colsOfLastGeneratedTiles = [];
var searchParams = new URLSearchParams(window.location.search);

$(document).ready(function () {
    var timeRemaining = Number($('.timer').text());

    setHintAction();

    if (checkCommand("hint"))
        showHint();

    decrementTimer(timeRemaining);
    getLastGeneratedTiles();
    $('.data').remove();
    getRowPositions();

    dropLastGeneratedTiles(rowsOfLastGeneratedTiles, colsOfLastGeneratedTiles);
});

function setHintAction() {
    if(Number($('#score').text()) < 1500){
        $('#hint').addClass("hoverRed");

        $('#hint').click(function (e) {
            e.preventDefault();
            $('#message').html("C'mon it's easy enough. You need at least 1500 points to use a hint.");
        });
    }else if (Number($('.timer').text()) <= 0){
        $('#hint').click(function (e) {
            e.preventDefault();
        });

    }else{
        $('#hint').removeClass("hoverRed").addClass("hoverGreen");
    }
}

function getLastGeneratedTiles() {
    $('.lastGeneratedTiles>div').map(function(){
        rowsOfLastGeneratedTiles.push($(this).attr('data-row'));
        colsOfLastGeneratedTiles.push($(this).attr('data-col'));
    });
}

function getRowPositions() {
    for (var i = 0; i <= 9; i++){
        positions[i] = $('.row'+i+' img').position().top;
    }
}

function decrementTimer(timeRemaining){
    if (Number($('.timer').text()) <= 0){
        $('.timer').css({color: "red"});
        $('.timer').html("0");
        return;
    }

    setInterval(function() {
        timeRemaining -= 1;
        if(timeRemaining <= 10)
            $('.timer').css({color: "red"});

        if (timeRemaining <= 0) {
            timeRemaining = 0;
            window.location.search = "";
        }

        $('.timer').html(timeRemaining);
    }, 1000);
}

function dropLastGeneratedTiles(rows, cols) {
    for (var i = 0; i < rows.length; i++){
        $('.row'+rows[i]+'.col'+cols[i]+' img').animate({
            opacity: 0,
            top: "-=" + positions[rows[i]] + "px",
        }, 0);

        $('.row'+rows[i]+'.col'+cols[i]+' img').animate({
            opacity: 1,
            top: "+=" + positions[rows[i]] + "px",
        }, (rows[0]*100)+500-(rows[i]*100));
    }
}

function select(row, col) {
    if (Number($('.timer').text()) <= 0) return;

    if (srcCoordinates.length === 0) {
        srcCoordinates = [row, col];
        $('.row'+row+'.col'+col+' img').animate({
            width: '30px',
            height: '30px'
        }, 200).removeClass('hoverAnimation');
    }else{
        if (row === srcCoordinates[0] && col === srcCoordinates[1]){
            srcCoordinates.length = 0;
            $('.row'+row+'.col'+col+' img').animate({
                width: '40px',
                height: '40px'
            }, 100).addClass('hoverAnimation');
        } else {
            destCoordinates = [row, col];
            swapTilesIfPossible();
        }
    }
}

function showHint() {
    $('.shape').css({opacity: 0.3});
    $('.hint img').animate({
        opacity: 1,
    }, 200);
    setTimeout(function () {
        $('.shape').animate({
            opacity: 1,
        }, 200);
    }, 2000);
}

function checkCommand(commandName){
    if (searchParams.has("command") && searchParams.get("command") === commandName)
        return true;
    else
        return false;
}

function swapTilesIfPossible() {
    window.location.search =
        '&srcRow='+srcCoordinates[0]+
        '&srcCol='+srcCoordinates[1]+
        '&destRow='+destCoordinates[0]+
        '&destCol='+destCoordinates[1]
    ;
}