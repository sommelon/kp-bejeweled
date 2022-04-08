var positions = [];
var srcCoordinates = [];
var destCoordinates = [];
var rowsOfLastGeneratedTiles = [];
var colsOfLastGeneratedTiles = [];
var rowsOfCombosToRemove = [];
var colsOfCombosToRemove = [];
var searchParams = new URLSearchParams(window.location.search);
var swapIsPossible = false;

$(document).ready(function () {

    $('.lastGeneratedTiles>div').map(function(){
        rowsOfLastGeneratedTiles.push($(this).attr('data-row'));
        colsOfLastGeneratedTiles.push($(this).attr('data-col'));
    });

    $('.combosToRemove>div').map(function(){
        rowsOfCombosToRemove.push($(this).attr('data-row'));
        colsOfCombosToRemove.push($(this).attr('data-col'));
    });

    swapIsPossible = $('.canSwap').attr('data-swap-is-possible');
    console.log('swap is possible = '+swapIsPossible)

    console.log(searchParams.get('command'));
    if (searchParams.has('command') && searchParams.get('command') === "check") {
        if (swapIsPossible){
            removeCombos(rowsOfCombosToRemove, colsOfCombosToRemove);
        }else{
            $('#message').html('Swap is not possible.');
        }
    }
    // $('.data').remove();

    for (var i = 0; i <= 9; i++){
        positions[i] = $('.row'+i+' img').position().top;
    }

    dropLastGeneratedTiles(rowsOfLastGeneratedTiles, colsOfLastGeneratedTiles);
});

function dropLastGeneratedTiles(rows, cols) {
    for (var i = 0; i < rows.length; i++){
        $('.row'+rows[i]+'.col'+cols[i]+' img').animate({
            opacity: 0,
            top: "-=" + positions[rows[i]] + "px",
        }, 0);

        $('.row'+rows[i]+'.col'+cols[i]+' img').animate({
            opacity: 1,
            top: "+=" + positions[rows[i]] + "px",
        }, 1400-(rows[i]*100));
    }
}

function removeCombos(rows, cols) {
    for (var i = 0; i < rows.length; i++){
        $('.row'+rows[i]+'.col'+cols[i]+' img').animate({
            width: "50px",
            height: "50px",
        }, 200);

        setTimeout(function() {
            $('.row' + rows[i] + '.col' + cols[i] + ' img').animate({
                width: "0px",
                height: "0px",
            }, 300);
        }, 200);
        swapTilesIfPossible();
    }
}

function select(row, col) {
    if (srcCoordinates.length === 0) {
        srcCoordinates = [row, col];
        $('.row'+row+'.col'+col+' img').animate({
            width: '30px',
            height: '30px'
        }, 500)
    }else{
        if (row === srcCoordinates[0] && col === srcCoordinates[1]){
            srcCoordinates.length = 0;
            $('.row'+row+'.col'+col+' img').animate({
                width: '40px',
                height: '40px'
            }, 100)
        }else {
            destCoordinates = [row, col];
            isTheSwapPossible();
        }
    }
}

function isTheSwapPossible() {
    window.location.search = '?command=check'+
        '&srcRow='+srcCoordinates[0]+
        '&srcCol='+srcCoordinates[1]+
        '&destRow='+destCoordinates[0]+
        '&destCol='+destCoordinates[1]
    ;
}

function swapTilesIfPossible() {
    window.location.search =
        '&srcRow='+srcCoordinates[0]+
        '&srcCol='+srcCoordinates[1]+
        '&destRow='+destCoordinates[0]+
        '&destCol='+destCoordinates[1]
    ;
}