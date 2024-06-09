import React,{useEffect,useState} from 'react';
import { ResponsiveBar } from '@nivo/bar'
import './App.css';
import Select from 'react-select'
import axios from 'axios'
// let data = [
 

function App() {  
  const [backendData,setBackendData] = useState([{}])  
  const [userChoice, setUserChoice] = useState("getAll")
  
  const [espera,SetEspera] = useState(true)  
  useEffect ( () =>{
    obtenerDatos();
  },[])
  
  function obtenerDatos(){
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'ngrok-skip-browser-warning' : "69420"
      };
    fetch("/getAll",headers).then(response => {
      return response.json()
    }).then(
      d => {
        
        setBackendData(d)
        SetEspera(false);
        
      }
    )
  }
  // console.log(JSON.parse(backendData.message));
  const selectOptions = [
    { value: 'getAll', label: 'Ultima Semana'  },
    { value: 'dia', label: 'Ultimo Dia' }
]
  if(espera)
    return "Cargando";
  return (
    <>
    <div className="App">
      <header className="App-header">
        <a>
          Dashboard
        </a>
      </header>
    </div>
    <br/>
    <div className="app">
        <Select
          isClearable={false}
          className='react-select'
          classNamePrefix='select'
          options={selectOptions}
          onChange={(choice) => setUserChoice(choice.value) }
    />
      {(typeof backendData.m === 'undefined')?(<p>Cargando</p>) :(
        <div>          
            <p className='p'>Lecturas ultimo {userChoice == "getAll"?"Semana":"Dias"}</p>
            {console.log(backendData)}
            {Barra(JSON.parse(JSON.stringify(backendData.m)),userChoice)}
            <hr/>
        </div>
      )}
    </div>
    </>
  );
   
function Barra(d,choice) {
  
  let data = d;
  for (let i = 0; i < data.length; i++) {
    
    if(choice == "getAll"){

      var today = new Date(data[i].fecha);
      var dd = String(today.getDate()).padStart(2, '0');
      var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
      var yyyy = today.getFullYear();

      today = mm + '/' + dd + '/' + yyyy;
      data[i].fecha = today;
      var seenNames = {};
      data = data.filter(function(currentObject) {
        if (currentObject.fecha in seenNames) {
            return false;
        } else {
            seenNames[currentObject.fecha] = true;
            return true;
        }
});
    }
    else{
      var today = new Date(data[i].fecha);
      var dd = String(today.getSeconds()).padStart(2, '0');
      var mm = String(today.getMinutes()).padStart(2, '0'); //January is 0!
      var yyyy = today.getHours();

      today = yyyy+"-"+mm+"-"+dd;
      data[i].fecha = today;
    }
  }
 
  return (
    <div style={{height:400}}>
        <ResponsiveBar
      data={data}
      keys={["lectura"]}
      indexBy="fecha"
      margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
      padding={0.4}
      valueScale={{ type: "linear" }}
      colors="#3182CE"
      animate={true}
      enableLabel={false}
      axisTop={null}
      axisRight={null}
      axisLeft={{
        tickSize: 5,
        tickPadding: 5,
        tickRotation: 0,
        legend: "Lecturas",
        legendPosition: "middle",
        legendOffset: -40
      }}
      axisBottom={{
        tickSize: 5,
        tickPadding: 5,
        tickRotation: 0,
        legend: "Fecha",
        legendPosition: "middle",
        legendOffset: 40
      }}
    />
    </div>
  );
}
}


export default App;

