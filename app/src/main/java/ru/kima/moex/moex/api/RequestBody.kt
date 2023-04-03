package ru.kima.moex.moex.api

class RequestBody {
    var body : String = String()
        private set

    enum class EngineType(val id : Int){
        stock(1),           //Фондовый рынок и рынок депозитов
        state(2),           //Рынок ГЦБ (размещение)
        currency(3),        //Валютный рынок
        futures(4),         //Срочный рынок
        commodity(5),	    //Товарный рынок
        interventions(6),   //Товарные интервенции
        offboard(7),	        //ОТС-система
        agro(9),	            //Агро
        otc(1012)	        //OTC Система
    }
    enum class MarketType(val id : Int){
        index	          (5),      //Индексы фондового рынка
        shares	          (1	),     //Рынок акций
        bonds	          (2	),     //Рынок облигаций
        ndm 	          (4	),     //Режим переговорных сделок
        otc 	          (29),     //ОТС
        ccp	              (27),     //РЕПО с ЦК
        deposit	          (35),     //Депозиты с ЦК
        repo	          (3	),     //Рынок сделок РЕПО
        qnv	              (28),     //Квал. инвесторы
        mamc	          (36),     //Мультивалютный рынок смешанных активов
        foreignshares	  (47),     //Иностранные ц.б.
        foreignndm	      (49),     //Иностранные ц.б. РПС
        moexboard	      (33),     //MOEX Board
        gcc	              (46),     //РЕПО с ЦК с КСУ
        credit	          (54),     //Рынок кредитов
        nonresndm	      (1015),   //Режим переговорных сделок (нерезиденты)
        nonresrepo	      (1017),   //Рынок РЕПО (нерезиденты)
        nonresccp	      (1019),   //Рынок РЕПО с ЦК (нерезиденты)
        standard	      (23),     //Standard
        classica          (25)      //classica
    }

    enum class OutputExtension(val ext : String){
        None (""),
        Json (".json"),
        Xml (".xml")
    }

    constructor(){
        body = "https://iss.moex.com/iss"
    }

    fun Securities(outputType : OutputExtension = OutputExtension.None) : RequestBody {
        body += "/securities"
        body += outputType.ext
        return this
    }

    fun Security(securityName : String, outputType : OutputExtension = OutputExtension.None) : RequestBody{
        body += '/'
        body += securityName
        body += outputType.ext
        return this
    }

    fun Aggregates(outputType : OutputExtension = OutputExtension.None) : RequestBody{
        body += "/aggregates"
        body += outputType.ext
        return this
    }

    fun Engines() : RequestBody{
        body += "/engines"
        return this
    }

    fun Engine(engineType : EngineType, outputType : OutputExtension = OutputExtension.None) : RequestBody{
        body += '/'
        body += engineType.name
        body += outputType.ext
        return this
    }

    fun  Markets() : RequestBody{
        body += "/markets"
        return this
    }

    fun Market(marketType : MarketType, outputType : OutputExtension = OutputExtension.None) : RequestBody{
        body += '/'
        body += marketType.name
        body += outputType.ext
        return this
    }

}