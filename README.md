# capp_SampleCardReader

This application demonstrates usage of CredenceID CardReader APIs. It covers the basics of using both synchronouse and
asynchronous APIs for reading and writing to MiFare cards. We explictly use MiFare cards because they act like storage
devices and allow the most versatile usage for demonstration purposes. This sample application demontrates the usage of
the following APIs.


```java
cardOpenCommand(new Biometrics.CardReaderStatusListener() {
    @Override
		public void onCardReaderOpen(ResultCode resultCode) {
		}

		Override
		public void onCardReaderClosed(ResultCode resultCode,
		                               CloseReasonCode closeReasonCode) {
	  }
});
```

```java
registerCardStatusListener(new Biometrics.OnCardStatusListener() {
    @Override
		public void onCardStatusChange(String ATR, int prevState, int currentState) {
		
		}
});
```

```java
cardCommand(ApduCommand, boolean, new Biometrics.OnCardCommandListener() {
    @Override
		public void onCardCommandComplete(ResultCode resultCode, byte sw1, byte sw2, byte[] data) {
				
		}
});
```

```java
cardCommandSync(new ApduCommand(""), boolean, int);
```

```java
cardCloseCommand();
```

# Important For Developers
In order to have your code integrated into this application is MUST follow a set
of guidelines. They may seem strict, but this is to ensure the utmost perfect 
code quality, logic, and simplicity.

1.  All comments MUST follow proper grammar, punctuation, and use of English(US)
		language.

2.  All single line comments must follow the format..
    ```java
    /* This is a comment explaining something cool. */
    ```

3.  All multiline comments MUST follow the format...
    ```java
    /* This comment might explain a variable in depth, or it might explain a few
		 * lines of code. Either ways this format should be used.
     */
     ```

4.  All lines of existing code that wish to be comments out must be commented 
		using Android's comment shortcut <Ctrl-/>. This assures proper formatting. 
		Notice how after using this shortcut the comments lines look as such, with a
		noticeable gap...
    ```java
     //        private String WIFI_SSD_CREDENCEAIR = "Credence Air";
     //        private String WIFI_SSD_SQUIRREL = "Squirrel";
     ```

5.  All code MUST be formatted by pressing <Shift-Ctrl-Alt-l>. When the popup
		comes up please select all checkboxes with the following titles:

    "Whole File"
    "Optimize Imports"
    "Rearrange Code"

    This will ensure all code is cleaned, formatted, and organized in a logical
		way.

6.  Every line of code must have a good reason for existing.

7.  All methods must be commented using Java's method commenting style 
		guideline. Method declarations should also following the
		following style.
```java
    /* This method adds to numbers together and returns your their sum.
     *
     * @param one first number to use for sum
     * @param two second number to use for sum
     * @return summation of two numbers
     */
     public int
     add(final int one,
     	 final int two){
        return (one + two);
     }
 ```

     NOTES: Notice how all code was shorthanded as much as possible. Every 
		 single line exists for a reason and there is nothing more and nothing less.
		 
		 Also note how the method was defined, with the method name on a new line.
		 If there is more then one parameter, each parameter is on a new line.

8.  All global class variables MUST have a line of comment explaining their
		purpose.

9.  Every single|few lines of code should have comments explaining what they do.

10. Methods may NOT exceed past 30 lines of code. This ONLY includes ACTUAL
		lines of code. This does NOT include the method name, method parameters, 
		opening|closing brackets, comments, empty lines, etc. If a method must exceed
		more then 30 lines, it must do so with very good reason.
