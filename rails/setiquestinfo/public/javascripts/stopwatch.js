var sec = 0;
var min = 0;
var lab = label;
function stopwatch(label) {
                if(label) lab = label;
		sec++;
		if (sec == 60) {
			sec = 0;
			min += 1;
		}
		totalTime = ((min<=9) ? "0" + min : min) + "m : " + ((sec<=9) ? "0" + sec : sec) + "s";
		document.getElementById("timer").innerHTML = lab + totalTime;
		start = setTimeout("stopwatch()", 1000);
	}
