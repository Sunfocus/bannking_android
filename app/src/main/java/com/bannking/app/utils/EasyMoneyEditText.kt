package com.bannking.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import com.bannking.app.R
import com.zeugmasolutions.localehelper.Locales
import java.text.DecimalFormat
import java.util.*

/**
 * The EditText widget for support of money requirements like currency, number formatting, comma formatting etc.
 *
 * Add com.wajahatkarim3.easymoneywidgets.EasyMoneyEditText into your XML layouts and you are done!
 * For more information, check http://github.com/wajahatkarim3/EasyMoney-Widgets
 *
 * @author Wajahat Karim (http://wajahatkarim.com)
 * @version 1.0.0 01/20/2017
 */
class EasyMoneyEditText : EditText {
    private var _currencySymbol: String? = null
    private var _showCurrency = false
    private var _showCommas = false

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        updateValue(text.toString())
    }

    @SuppressLint("CustomViewStyleable")
    private fun initView(context: Context, attrs: AttributeSet?) {
        // Setting Default Parameters
        _currencySymbol = Currency.getInstance(Locale.getDefault()).symbol
        _showCurrency = true
        _showCommas = true

        // Check for the attributes
        if (attrs != null) {
            // Attribute initialization
            val attrArray =
                context.obtainStyledAttributes(attrs, R.styleable.EasyMoneyWidgets, 0, 0)
            try {
                var currnecy = attrArray.getString(R.styleable.EasyMoneyWidgets_currency_symbol)
                if (currnecy == null) currnecy = Currency.getInstance(Locale.getDefault()).symbol
                setCurrency(currnecy)
                _showCurrency =
                    attrArray.getBoolean(R.styleable.EasyMoneyWidgets_show_currency, true)
                _showCommas = attrArray.getBoolean(R.styleable.EasyMoneyWidgets_show_commas, true)
            } finally {
                attrArray.recycle()
            }
        }

        // Add Text Watcher for Decimal formatting
        initTextWatchers()
    }

    private fun initTextWatchers() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                removeTextChangedListener(this)
                val backupString = charSequence.toString()
                try {
                    var originalString = charSequence.toString()
                    val longval: Long
                    originalString = valueString
                    longval = originalString.toLong()
                    val formattedString = getDecoratedStringFromNumber(longval)

                    //setting text after format to EditText
                    setText(formattedString)
                    setSelection(text.length)
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                    setText(backupString)
                    val valStr = valueString
                    if (valStr == "") {
                        val `val`: Long = 0
                        //                        setHint(getResources().getString(R.string.str_enter_amount));
//                        setText(getDecoratedStringFromNumber(val));
                    } else {
                        // Some decimal number
                        if (valStr.contains(".")) {
                            if (valStr.indexOf(".") == valStr.length - 1) {
                                // decimal has been currently put
                                val front = getDecoratedStringFromNumber(
                                    valStr.substring(
                                        0,
                                        valStr.length - 1
                                    ).toLong()
                                )
                                setText("$front.")
                            } else {
                                val nums = valueString.split(".").toTypedArray()
                                val front = getDecoratedStringFromNumber(nums[0].toLong())
                                setText(front + "." + nums[1])
                            }
                        }
                    }
                    setSelection(text.length)
                }
                addTextChangedListener(this)
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun updateValue(text: String) {
        setText(text)
    }

    private fun getDecoratedStringFromNumber(number: Long): String {
        val numberPattern = "#,###,###,###"
        var decoStr = ""
        val formatter = DecimalFormat.getInstance(Locales.English) as DecimalFormat
        if (_showCommas && !_showCurrency) formatter.applyPattern(numberPattern) else if (_showCommas && _showCurrency) formatter.applyPattern(
            "$_currencySymbol $numberPattern"
        ) else if (!_showCommas && _showCurrency) formatter.applyPattern("$_currencySymbol ") else if (!_showCommas && !_showCurrency) {
            decoStr = number.toString() + ""
            return decoStr
        }
        decoStr = formatter.format(number)
        return decoStr
    }//        String string = String.format(Locale.ENGLISH, getText().toString());

    /**
     * Get the value of the text without any commas and currency.
     * For example, if the edit text value is $ 1,34,000.60 then this method will return 134000.60
     * @return A string of the raw value in the text field
     */
    val valueString: String
        get() {
            var string = text.toString()
            //        String string = String.format(Locale.ENGLISH, getText().toString());
            if (string.contains(",")) {
                string = string.replace(",", "")
            }
            if (string.contains(" ")) {
                string = string.substring(string.indexOf(" ") + 1, string.length)
            }
            return string
        }

    /**
     * Get the value of the text with formatted commas and currency.
     * For example, if the edit text value is $ 1,34,000.60 then this method will return exactly $ 1,34,000.60
     * @return A string of the text value in the text field
     */
    val formattedString: String
        get() = text.toString()

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     * @param newSymbol the new symbol of currency in string
     */
    fun setCurrency(newSymbol: String?) {
        _currencySymbol = newSymbol
        updateValue(text.toString())
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     * @param locale the locale of new symbol. (Defaul is Locale.US)
     */
    fun setCurrency(locale: Locale?) {
        setCurrency(Currency.getInstance(locale).symbol)
    }

    /**
     * Set the currency symbol for the edit text. (Default is US Dollar $).
     * @param currency the currency object of new symbol. (Defaul is Locale.US)
     */
    fun setCurrency(currency: Currency) {
        setCurrency(currency.symbol)
    }

    /**
     * Whether currency is shown in the text or not. (Default is true)
     * @return true if the currency is shown otherwise false.
     */
    var isShowCurrency: Boolean
        get() = _showCurrency
        private set(value) {
            _showCurrency = value
            updateValue(text.toString())
        }

    /**
     * Shows the currency in the text. (Default is shown).
     */
    fun showCurrencySymbol() {
        isShowCurrency = true
    }

    /**
     * Hides the currency in the text. (Default is shown).
     */
    fun hideCurrencySymbol() {
        isShowCurrency = false
    }

    /**
     * Shows the commas in the text. (Default is shown).
     */
    fun showCommas() {
        _showCommas = true
        updateValue(text.toString())
    }

    /**
     * Hides the commas in the text. (Default is shown).
     */
    fun hideCommas() {
        _showCommas = false
        updateValue(text.toString())
    }
}