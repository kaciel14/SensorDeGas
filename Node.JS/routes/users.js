const mysql = require('mysql');


var con = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "Mauhalo#2212",
    database: "gasDetector",
    dateStrings: true
  });

  con.connect(function(err) {
    if (err) throw err;
    console.log("Connected!");
  });

  let userModel = {};

  userModel.getLectures = (callBack) =>{
    if(con){
        con.query(
            "SELECT * FROM Usuario ORDER BY fecha",
            (err, rows) => {
                if(err){
                    throw err;
                }else{
                    callBack(null, rows);
                }
            }
        );
    }
};


  userModel.insertLecture = (userData, callBack) => {

    if(con){
        con.query('INSERT INTO Usuario SET ?', userData,
        (err, result) => {
            if(err){
                throw err;
            }else{
                callBack(null, {
                    'insertedValue' : result.valor
                });
            }
        }
        );
    }
};

module.exports = userModel;