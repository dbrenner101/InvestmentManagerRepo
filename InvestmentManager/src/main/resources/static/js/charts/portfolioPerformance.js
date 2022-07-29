var graphData;

// Labels along the x-axis
var dateLabels = new Array();
// For drawing the lines
var closeData = new Array();

var config = {
   type: 'line',
   data: {
     labels: [],
     datasets: [
       {
         data: [],
         label: "",
         fill: false,
         borderColor: 'rgb(75, 192, 192)',
         tension: 0.1
       }]
   }
 };

var ctx = document.getElementById("historicalPerformanceChart").getContext("2d");
const myChart = new Chart(document.getElementById("historicalPerformanceChart"), config);


document.getElementById('investmentSelect').addEventListener('change', function() {
    loadChartData();
});


function loadChartData() {
    var xhttp = new XMLHttpRequest();

    var symbol = document.getElementById("investmentSelect").value;
    var numDays = document.querySelector('input[name="numMonths"]:checked').value;

    document.getElementById("historicalPerformanceChart").innerHTML = "";

    xhttp.onreadystatechange = function() {

        if (this.readyState == 4 && this.status == 200) {
            var response = JSON.parse(this.responseText);
            graphData = response

            var newDateLabels = new Array();
            var newCloseData = new Array();

            if (graphData != null) {
                for (let i=0; i < graphData.length; i++) {
                    newDateLabels[i] = graphData[i].quoteDate;
                    newCloseData[i] = graphData[i].marketValue;
                }
            }

            /* var jsonResult = document.getElementById("jsonResult");
            jsonResult.innerHTML=this.responseText; */

            removeData(myChart);
            addData(myChart, newDateLabels, newCloseData, symbol);
        }
    };
    xhttp.open("GET", "getPortfolioPerformanceAjax?symbol=" + symbol + "&numDays=" + numDays, true);
    xhttp.send();
}

function addData(chart, labels, data, symbol) {
    var newDataSet = {
        label: 'New Label',
        label: symbol + " Performance",
        backgroundColor: "#457fa3",
        fill: false,
        data: data
    };
    chart.data.labels = labels;
    config.data.datasets.push(newDataSet);
    myChart.update();
}

function removeData(chart) {
    chart.data.labels.pop();
    config.data.datasets.pop();
    myChart.update();
}

function buildChart() {
    var ctx = document.getElementById("historicalPerformanceChart").getContext("2d");
    var myChart = new Chart(ctx, config);
}