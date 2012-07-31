aryImages = new Array();
aryImages[0] = new Image();
aryImages[0].src = "images/sample1.png";
aryImages[1] = new Image();
aryImages[1].src = "images/sample1-signal.png";

function over_button(e)
{
  document.getElementById('sample').src = aryImages[0].src;
}

function off_button(e)
{
  document.getElementById('sample').src = aryImages[1].src;
}



