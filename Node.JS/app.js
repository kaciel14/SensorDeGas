//Paquete para crear un servidor.
var express = require('express');
const date = require('date-and-time')
const usuarioC = require('./routes/users');
var cors = require('cors');

//Tokens de TELEGRAM.
const TelegramBot = require('node-telegram-bot-api');
const token = '5991600174:AAEFmDz5vPujebgyoGYW5fzAbd45CNtP_Bs';

const bot = new TelegramBot(token, { polling: false });
const chatId = '-1001873100910';
const message = 'Hola, de nuevo';

//Lectura del sensor.
var value = 0;

//ON / OFF
var response = "";


//Se inicia el servidor. Puerto 3000.
var app = express();
app.use(express.json());


app.use(cors());

app.listen(3000, () => {
  console.log('Wuu!')
})


app.get('/samples', (req, res) => {
  res.status(200).send({
    title: "Este es un get",
    data: value
  })
});

app.get('/getAll', (req, res) => {
  usuarioC.getLectures((err, data) => {
    res.json(data);
  });
})

//Esta petición la hace Android.

// Req. Es la peticion (Los datos que recibes)
// Res. Es la respuesta que le da el servidor a quien le mando la informacion.

app.post("/sendLecture", (req, res)=>{
  console.log(req.body)

  if(response != "off"){

    res.status(200).json({
      message:"Data recibida",
      data:req.body
    })

    // Se almacena el valor de la lectura del sensor.
    value = req.body.value;

    // Se manda el mensaje por telegram.
    bot.sendMessage(chatId, "Deteccion de gas: " + value + "!!");
  }
  else{
    //Las notificaciones estan apagadas.
    res.status(200).json({
      message:"Las notificaciones esta detenidas, el usuario debe activarlas",
    })
  
    value = "-1"
  }
})



app.post("/updateValue", (req, res)=>{

  console.log(req.body)
  
    // Se almacena el valor de la lectura del sensor.
    value = req.body.value


    var datetime = new Date();

    let horas = datetime.getHours();
    let minutos = datetime.getMinutes();
    let segundos = datetime.getSeconds()
    
    var toSendDate = datetime.toISOString().slice(0,10) + ' ' + (horas) + ':' + minutos + ':' + segundos;

    const userData = {
      lectura: req.body.value,
      fecha: toSendDate
    };

    usuarioC.insertLecture(userData, (err, data) => {

      if(data){
          res.json({
              succes: true,
              msg: 'Lectura Insertada',
              data: req.body
          });
      }else{
          res.status(500).json({
              success:false,
              msg:'Error'
          });
      }
  });

})

//Esta petición la hace Telegram desde Android.
app.post("/sendUserCommand", (req, res)=>{
  console.log(req.body)

  res.status(200).json({
    message:"Mensaje de usuario Recibido",
    bodyOfMessage:req.body
  })

  response = req.body.msj
  console.log(response)

})


