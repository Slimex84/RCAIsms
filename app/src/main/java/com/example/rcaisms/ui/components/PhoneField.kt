package com.example.rcaisms.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlin.jvm.javaClass
import kotlin.text.count
import kotlin.text.indexOf
import kotlin.text.isEmpty
import kotlin.text.substring
import kotlin.text.take

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneField(
    phone: String,
    label: @Composable () -> Unit,
    mask: String = "000 000 00 00",
    maskNumber: Char = '0',
    onPhoneChanged: (String) -> Unit
) {
    // OutlinedTextField with a visual transformation that shows a phone mask.
    // onValueChange limits the raw input length to the number of mask digits.
    OutlinedTextField(
        value = phone,
        onValueChange = { it ->
            // Keep at most the number of "digit slots" defined by the mask.
            onPhoneChanged(it.take(mask.count { it == maskNumber }))
        },
        label = label,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        visualTransformation = PhoneVisualTransformation(mask, maskNumber),
    )
}

class PhoneVisualTransformation(val mask: String, val maskNumber: Char) : VisualTransformation {

    // maximum number of user digits allowed (count of placeholders)
    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        // trim input to maxLength
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        // build displayed string by walking mask and input digits
        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0
            while (textIndex < trimmed.length && maskIndex < mask.length) {
                // if current mask char is a literal (not a placeholder), append the literal chunk
                if (mask[maskIndex] != maskNumber) {
                    // find the next placeholder index (assumes there is one)
                    val nextDigitIndex = mask.indexOf(maskNumber, maskIndex)
                    // append all literal chars between maskIndex and nextDigitIndex
                    append(mask.substring(maskIndex, nextDigitIndex))
                    maskIndex = nextDigitIndex
                }
                // append one input digit and advance both indices
                append(trimmed[textIndex++])
                maskIndex++
            }
        }

        // return transformed text and an OffsetMapping that maps cursor positions correctly
        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber))
    }

    // equals/hashCode are overridden because VisualTransformation can be used as a key
    override fun hashCode(): Int {
        return mask.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhoneVisualTransformation

        if (mask != other.mask) return false
        if (maskNumber != other.maskNumber) return false
        if (maxLength != other.maxLength) return false

        return true
    }
}

private class PhoneOffsetMapper(val mask: String, val numberChar: Char) : OffsetMapping {

    // originalToTransformed: given an offset in raw text (digits only), return offset in displayed text
    override fun originalToTransformed(offset: Int): Int {
        var noneDigitCount = 0
        var i = 0
        // Count how many literal mask characters appear before the position corresponding to 'offset' digits.
        while (i < offset + noneDigitCount) {
            if (mask[i++] != numberChar) noneDigitCount++
        }
        return offset + noneDigitCount
    }

    // transformedToOriginal: given an offset in displayed text, compute how many real digits are before it
    override fun transformedToOriginal(offset: Int): Int =
        offset - mask.take(offset).count { it != numberChar }
}
