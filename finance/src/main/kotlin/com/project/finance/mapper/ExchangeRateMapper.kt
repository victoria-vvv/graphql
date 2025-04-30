package com.project.finance.mapper

import com.project.finance.model.ExchangeRate
import com.project.finance.util.GlobalIdUtils
import org.springframework.stereotype.Component

@Component
class ExchangeRateMapper:CommonMapper<com.project.finance.model.tables.records.ExchangeRateRecord, ExchangeRate> {

    override fun mapToDto(record: com.project.finance.model.tables.records.ExchangeRateRecord): ExchangeRate {
        return ExchangeRate(
            id = GlobalIdUtils.toGlobalId("ExchangeRate", record.id.toString()),
            baseCurrency = record.baseCurrency,
            targetCurrency = record.targetCurrency,
            rate = record.rate,
            rateDate = record.rateDate
        )
    }

    override fun supports(typeName: String): Boolean {
        return typeName == "ExchangeRate"
    }
}