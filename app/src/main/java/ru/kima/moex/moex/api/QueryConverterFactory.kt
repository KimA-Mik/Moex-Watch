package ru.kima.moex.moex.api

import android.provider.ContactsContract.Data
import android.text.format.DateFormat
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type
import java.util.Date

class QueryConverterFactory private constructor() : Converter.Factory() {

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        if (type == Data::class.java) {
            return DateRequestBodyConverter.INSTANCE
        }
        return null
    }

    companion object {
        fun create(): QueryConverterFactory {
            return QueryConverterFactory()
        }
    }
}

internal class DateRequestBodyConverter private constructor() : Converter<Date, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: Date): RequestBody? {
        return RequestBody.create(MEDIA_TYPE, DateFormat.format("yyyy-MM-dd", value).toString())
    }

    companion object {
        val INSTANCE = DateRequestBodyConverter()
        private val MEDIA_TYPE = MediaType.get("text/plain; charset=UTF-8")
    }
}
