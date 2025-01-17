//#include <SoftwareSerial.h>
#include <Tone.h>

int gasPin = 2;
int gasVal = 0;
int ledPin = 13;
int buzzerPin = 12;
int data = 1; // for incoming serial data


//SoftwareSerial BTSerial(10, 11);
#define HC06 Serial3

void setup() {
  pinMode(gasPin, INPUT);
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  pinMode(buzzerPin, OUTPUT);
  Serial.begin(9600);
  HC06.begin(9600);
}

void loop() {


  // Comprobamos si hay algo disponible en el puerto serial de Bluetooth.
  if (HC06.available() > 0) {
    data = HC06.read(); 
    Serial.println(data); 
  }



  // El numero que recibamos es el estado del servidor, es decir, si recibe o no las lecturas.
  // Y el LED es el indicador de dicho estado.
  if(data == '0'){
    digitalWrite(ledPin, LOW);
    HC06.println("Led OFF");
  }

  else if(data == '1'){
    digitalWrite(ledPin, HIGH);
    HC06.println("Led ON");

  }



  // Leemos el PIN en el que está conectado el sensor.

  gasVal = analogRead(A0);
  Serial.println(gasVal);
  Serial.println("--ONLY HERE--");

  // Si la lectura pasa de cierto umbral, mandamos la letura por Bluetooth
  // y activamos el buzzer.

  if (gasVal >= 380) {

    digitalWrite(ledPin, HIGH);
    delay(1000);
    digitalWrite(ledPin, LOW);


    Serial.println(gasVal);
    HC06.println(gasVal);


    //HC06.println("Gas detected!");
    Serial.println("Gas detected!");
    
   //tone(buzzerPin, 400, 1000);


   // Si no, desactivamos el buzzer y no se realiza ninguna acción.

  } else {
    digitalWrite(ledPin, LOW);
    noTone(buzzerPin);
  }

  delay(5000);

}


// timers TC0 TC1 TC2   channels 0-2 ids 0-2  3-5  6-8     AB 0 1
// use TC1 channel 0 
#define TONE_TIMER TC1
#define TONE_CHNL 0
#define TONE_IRQ TC3_IRQn

// TIMER_CLOCK4   84MHz/128 with 16 bit counter give 10 Hz to 656KHz
//  piano 27Hz to 4KHz

static uint8_t pinEnabled[PINS_COUNT];
static uint8_t TCChanEnabled = 0;
static boolean pin_state = false ;
static Tc *chTC = TONE_TIMER;
static uint32_t chNo = TONE_CHNL;

volatile static int32_t toggle_count;
static uint32_t tone_pin;

// frequency (in hertz) and duration (in milliseconds).

void tone(uint32_t ulPin, uint32_t frequency, int32_t duration)
{
		const uint32_t rc = VARIANT_MCK / 256 / frequency; 
		tone_pin = ulPin;
		toggle_count = 0;  // strange  wipe out previous duration
		if (duration > 0 ) toggle_count = 2 * frequency * duration / 1000;
		 else toggle_count = -1;

		if (!TCChanEnabled) {
 			pmc_set_writeprotect(false);
			pmc_enable_periph_clk((uint32_t)TONE_IRQ);
			TC_Configure(chTC, chNo,
				TC_CMR_TCCLKS_TIMER_CLOCK4 |
				TC_CMR_WAVE |         // Waveform mode
				TC_CMR_WAVSEL_UP_RC ); // Counter running up and reset when equals to RC
	
			chTC->TC_CHANNEL[chNo].TC_IER=TC_IER_CPCS;  // RC compare interrupt
			chTC->TC_CHANNEL[chNo].TC_IDR=~TC_IER_CPCS;
			 NVIC_EnableIRQ(TONE_IRQ);
                         TCChanEnabled = 1;
		}
		if (!pinEnabled[ulPin]) {
			pinMode(ulPin, OUTPUT);
			pinEnabled[ulPin] = 1;
		}
		TC_Stop(chTC, chNo);
                TC_SetRC(chTC, chNo, rc);    // set frequency
		TC_Start(chTC, chNo);
}

void noTone(uint32_t ulPin)
{
	TC_Stop(chTC, chNo);  // stop timer
	digitalWrite(ulPin,LOW);  // no signal on pin
}

// timer ISR  TC1 ch 0
void TC3_Handler ( void ) {
	TC_GetStatus(TC1, 0);
	if (toggle_count != 0){
		// toggle pin  TODO  better
		digitalWrite(tone_pin,pin_state= !pin_state);
		if (toggle_count > 0) toggle_count--;
	} else {
		noTone(tone_pin);
	}
}

