package com.cid.sample.cardreader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.credenceid.biometrics.ApduCommand;
import com.credenceid.biometrics.Biometrics;
import com.credenceid.biometrics.Biometrics.CloseReasonCode;
import com.credenceid.biometrics.Biometrics.ResultCode;
import com.credenceid.biometrics.BiometricsManager;
import com.credenceid.biometrics.CardCommandResponse;

import static com.credenceid.biometrics.Biometrics.ResultCode.FAIL;
import static com.credenceid.biometrics.Biometrics.ResultCode.OK;

@SuppressWarnings({"unused"})
public class MainActivity extends Activity {
	// If write was good (SW1, SW2) = (0x90, 0x00).
	// If you print out SW1 as an integer you will get "-112", this is due to "2's Complement".
	// You can either check SW1 by comparing is against "-112" or you may compare it against the
	// following two constants.
	private final static byte SW1_SUCCESS = (byte) 0x90;
	private final static byte SW2_SUCCESS = (byte) 0x00;
	private final String mTAG = MainActivity.class.getSimpleName();
	// ------------------------------------ READ APDU ------------------------------------
	// Reads 4096 (4K) number of bytes from card.
	private final String mAPDURead4k = "FF"    // MiFare Card
			+ "B0"                             // MiFare Card READ Command
			+ "00"                             // P1
			+ "00"                             // P2: Block Number
			+ "001000";                        // Number of bytes to read
	// Reads 2048 (2K) number of bytes from card.
	private final String mAPDURead2k = "FF"    // MiFare Card
			+ "B0"                             // MiFare Card READ Command
			+ "00"                             // P1
			+ "00"                             // P2: Block Number
			+ "000800";                        // Number of bytes to read
	// Reads 1024 (1K) number of bytes from card.
	private final String mAPDURead1k = "FF"    // MiFare Card
			+ "B0"                             // MiFare Card READ Command
			+ "00"                             // P1
			+ "00"                             // P2: Block Number
			+ "000400";                        // Number of bytes to read
	// ------------------------------------ WRITE APDU ------------------------------------
	// Writes 4096 (4K) number of bytes to card.
	private final String mAPDUWrite4k = "FF"   // MiFare Card
			+ "D6"                             // MiFare Card READ Command
			+ "00"                             // P1
			+ "00"                             // P2: Block Number
			+ "001000";                        // Number of bytes to read
	// Writes 2048 (2K) number of bytes to card.
	private final String mAPDUWrite2k = "FF"   // MiFare Card
			+ "D6"                             // MiFare Card READ Command
			+ "00"                             // P1
			+ "00"                             // P2: Block Number
			+ "000800";                        // Number of bytes to read
	// Writes 1024 (1K) number of bytes to card.
	private final String mAPDUWrite1k = "FF"   // MiFare Card
			+ "D6"                             // MiFare Card READ Command
			+ "00"                             // P1
			+ "00"                             // P2: Block Number
			+ "000400";                        // Number of bytes to read
	// ------------------------------------ READ APDU ------------------------------------
	// This APDU is used to read "mSpecialData" written to the card.
	private final String mAPDUReadSpecialData = "FF"  // MiFare Card
			+ "B0"                                    // MiFare Card READ Command
			+ "00"                                    // P1
			+ "01"                                    // P2: Block Number
			+ "0E";                                   // Number of bytes to read
	// -------------------------------- Special Data: Write to Card --------------------------------
	// This is the data we will write to the card "CredenceID LLC"
	private byte[] mSpecialData = new byte[]{
			// C	r	  e     d     e		n     c     e     I     D    ' '    L     L     C
			0x43, 0x72, 0x65, 0x64, 0x65, 0x6e, 0x63, 0x65, 0x49, 0x44, 0x20, 0x4c, 0x4c, 0x43};

	// Biometrics object used for calling Credence APIs.
	private BiometricsManager mBiometricsManager;
	private Context mContext;

	private String mAPDUCommand = mAPDURead1k;

	// Listener invoked each time C-Service detects a card change from card reader.
	private Biometrics.OnCardStatusListener onCardStatusListener = (String ATR,
																	int prevState,
																	int currentState) -> {
		// If currentState is 1, then no card is present.
		if (currentState == 1) {
			Log.d(mTAG, "Card was removed, no card present.");
			//noinspection UnnecessaryReturnStatement
			return;
		}

		// currentStates [2, 6] represent a card present. If a card is present, code will reach
		// here and attempt reads/writes with card.

		// Uncomment any one of three method calls below to see how to use an API.

		// This function will perform ASYNCHRONOUS card reads.
		// readCardAsync(mAPDURead1k);

		// This function will perform SYNCHRONOUS card reads.
		// readCardSync();

		// This function will perform ASYNCHRONOUS card writing.
		// writeCardAsync();

		// This function will perform SYNCHRONOUS card writing.
		//  writeCardSync();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		mBiometricsManager = new BiometricsManager(this);

		mBiometricsManager.initializeBiometrics((ResultCode resultCode,
												 String minVersion,
												 String currentVersion) -> {
			if (resultCode == OK) {
				Toast.makeText(mContext, "BM Initialized.", Toast.LENGTH_SHORT).show();

				// Once Biometrics has initialized we may now open card reader.
				openCardReader();
			}
		});
	}

	private void
	openCardReader() {
		final String localTag = mTAG + ":openCardReader";
		Log.d(mTAG, "openCardReader()");

		mBiometricsManager.cardOpenCommand(new Biometrics.CardReaderStatusListener() {
			@Override
			public void onCardReaderOpen(ResultCode resultCode) {
				if (resultCode == FAIL) {
					Log.e(localTag, "FAILED to open CardReader.");
					return;
				}

				// If card reader opened successfully, register a listener will be invoked each time
				// card reader's status changes. Meaning that anytime a card is placed/removed
				// invoke this callback.
				mBiometricsManager.registerCardStatusListener(onCardStatusListener);
			}

			@Override
			public void onCardReaderClosed(ResultCode resultCode,
										   CloseReasonCode closeReasonCode) {
				Log.d(localTag, "CardReader closed: " + closeReasonCode.name());
			}
		});
	}

	/* This method attempts to read 1K, 2K, and 4K blocks of data off of a card. You pass in either
	 * one of three mAPDURead(4|2|1)k commands. It will execute this command then try the next size
	 * block.
	 *
	 * ie. If you pass 1K command, it will next try the 2K then 4K.
	 * ie. If you pass 2K command, it will next try 4K.
	 * ie. If you pass 4K after command is done, it will do nothing.
	 *
	 * The purpose of this function is to demonstrate how you may execute multiple APDUs linearly
	 * using async. APIs. The core of this is a "state machine".
	 *
	 * @param APDUCommand Initial APDU command to execute.
	 */
	private void
	readCardAsync(String APDUCommand) {
		final String localTAG = mTAG + ":readCardAsync";
		Log.d(localTAG, "readCardAsync(String)");

		if (APDUCommand.equals("")) {
			Log.d(localTAG, "Empty APDU passed, nothing to do, exiting method.");
			return;
		}

		mBiometricsManager.cardCommand(new ApduCommand(APDUCommand),
				false,
				(ResultCode resultCode, byte sw1, byte sw2, byte[] data) -> {
					if (resultCode == FAIL) {
						Log.w(localTAG, "APDU Execution error(SW1, SW2):" + sw1 + ", " + sw2);
						return;
					}

					Log.d(localTAG, "SW1, SW2: " + Hex.toString(sw1) + ", " + Hex.toString(sw2));
					Log.d(localTAG, "Length of read data: " + data.length);
					Log.d(localTAG, "Data: " + Hex.toString(data));

					if (APDUCommand.equals(mAPDURead1k))
						readCardAsync(mAPDURead2k);
					else if (APDUCommand.equals(mAPDURead2k))
						readCardAsync(mAPDURead4k);
				});
	}

	/* This method attempts to read 1K, 2K, and 4K blocks of data off of a card. It will attempts to
	 * read each block of data linearly. It will first try 1K, then 2K, then 4K card reads.
	 *
	 * The purpose of this function is to demonstrate how you may execute multiple APDUs linearly
	 * using sync. APIs.
	 */
	private void
	readCardSync() {
		final String localTAG = mTAG + ":readCardSync";
		Log.d(localTAG, "readCardSync()");

		final String[] commandList = {mAPDURead1k, mAPDURead2k, mAPDURead4k};

		for (String command : commandList) {
			CardCommandResponse response =
					mBiometricsManager.cardCommandSync(new ApduCommand(command), false, 4000);

			// If APDU failed then response will be NULL. It is also possible to check SW1 and SW2.
			if (response == null) {
				Log.w(localTAG, "APDUCommand(" + command + "): NULL RESPONSE");
				return;
			}

			Log.d(localTAG, "SW1, SW2: " + Hex.toString(response.sw1) + ", " + Hex.toString(response.sw2));
			Log.d(localTAG, "Length of read data: " + response.data.length);
			Log.d(localTAG, "Data: " + Hex.toString(response.data));
		}
	}

	/* This method attempts to write some data to MiFare card. After writing data it will then try
	 * to read that same data back.
	 */
	private void
	writeCardSync() {
		final String localTAG = mTAG + ":writeCardSync";
		Log.d(localTAG, "writeCardSync()");

		String apdu = createWriteAPDUCommand((byte) 0x01, mSpecialData);
		CardCommandResponse response = mBiometricsManager.cardCommandSync(new ApduCommand(apdu), false, 4000);
		if (response == null) {
			Log.w(mTAG, "APDUCommand(" + apdu + "): NULL RESPONSE");
			return;
		}
		Log.d(localTAG, "SW1, SW2: " + Hex.toString(response.sw1) + ", " + Hex.toString(response.sw2));

		// After writing data to card we will not try to read that same data back!
		apdu = "FF"                   // MiFare Card
				+ "B0"                // MiFare Card READ Command
				+ "00"                // P1
				+ "01"                // P2: Block Number
				// "CredenceID LLC" is 14 bytes long. Fourteen in hex is "0E".
				+ "0E";               // Number of bytes to read;

		// Execute read command.
		response = mBiometricsManager.cardCommandSync(new ApduCommand(apdu), false, 3000);
		if (response == null) {
			Log.w(mTAG, "APDUCommand(" + apdu + "): NULL RESPONSE");
			return;
		}
		Log.d(localTAG, "SW1, SW2: " + Hex.toString(response.sw1) + ", " + Hex.toString(response.sw2));

		// Convert read data into human readable ASCII characters. Wrap data around quotes ''.
		String name = "\'";
		for (int i = 0; i < response.data.length; i++) {
			//noinspection StringConcatenationInLoop
			name += (char) response.data[i];
		}
		name += "\'";
		Log.d(mTAG, "Data read from card " + name);
	}

	/* This method attempts to write some data to MiFare card. After writing data it will then try
	 * to read that same data back.
	 */
	private void writeCardAsync() {
		final String localTAG = mTAG + ":writeCardAsync";
		Log.d(localTAG, "writeCardAsync()");

		String apdu = createWriteAPDUCommand((byte) 0x01, mSpecialData);
		mBiometricsManager.cardCommand(new ApduCommand(apdu),
				false,
				(ResultCode resultCode, byte sw1, byte sw2, byte[] data) -> {
					if (resultCode == FAIL) {
						Log.w(localTAG, "APDU Execution error(SW1, SW2):" + sw1 + ", " + sw2);
						return;
					}
					Log.d(localTAG, "SW1, SW2: " + Hex.toString(sw1) + ", " + Hex.toString(sw2));

					// Now use SYNCHRONOUS APIs for reading that data back.
					CardCommandResponse response = mBiometricsManager.cardCommandSync(new ApduCommand(mAPDUReadSpecialData), false, 4000);
					if (response == null) {
						Log.w(mTAG, "APDUCommand(" + apdu + "): NULL RESPONSE");
						return;
					}
					Log.d(localTAG, "SW1, SW2: " + Hex.toString(response.sw1) + ", " + Hex.toString(response.sw2));

					// Convert read data into human readable ASCII characters. Wrap data around quotes ''.
					String name = "\'";
					for (int i = 0; i < response.data.length; i++) {
						//noinspection StringConcatenationInLoop
						name += (char) response.data[i];
					}
					name += "\'";
					Log.d(mTAG, "Data read from card " + name);
				});
	}

	/* Creates an APDU command for writing data to a MiFare card.
	 *
	 * @param blockNumber	Block number to write data to.
	 * @param data 			Data to write to card.
	 * @return				APDU command in String format.
	 */
	@SuppressWarnings("SameParameterValue")
	private String
	createWriteAPDUCommand(byte blockNumber,
						   byte[] data) {
		final int dataLen = data.length;

		// 7 MiFare bytes, 2 Data size bytes, CID header bytes+ data
		byte[] writeAPDU = new byte[7 + dataLen];

		writeAPDU[0] = (byte) 0xFF;         // MiFare Card Header
		writeAPDU[1] = (byte) 0xD6;         // MiFare Card WRITE Command
		writeAPDU[2] = (byte) 0x00;         // P1
		writeAPDU[3] = blockNumber;         // P2: Block Number
		writeAPDU[4] = (byte) 0x00;         // Escape Character
		writeAPDU[5] = (byte) ((dataLen >> 8) & 0xFF);    // Number of bytes: MSB
		writeAPDU[6] = (byte) (dataLen & 0xFF);            // Number of bytes: LSB

		// Append "data" to end of "writeAPDU" byte array.
		System.arraycopy(data, 0, writeAPDU, 7, dataLen);

		// Return "writeAPDU" as a String.
		return Hex.toString(writeAPDU);
	}
}