package app.index.api.routing.stripe.routes

import app.index.api.routing.stripe.ProRoute
import app.index.core.logic.pro.ProManager
import io.github.smiley4.ktorswaggerui.dsl.resources.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.proPromotionCodeRoute() {
    val proManager by inject<ProManager>()

    get<ProRoute.PromotionCodeRoute.ValidateRoute>({
        tags = listOf("stripe")
        operationId = "stripe-validate-promotion-code"
        summary = "validates a promotion code, it checks if it's valid"
        request {
            queryParameter<String>("promotion_code") {
                required = true
                description = "the promotion code customer facing name, not the ID"
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "coupon is valid"
            }
            HttpStatusCode.Unauthorized to {
                description = "user not authenticated"
            }
            HttpStatusCode.NotFound to {
                description = "coupon not valid"
            }
        }
    }) {
        val valid = proManager.isPromotionCodeValid(it.promotion_code)

        call.respond(if (valid) HttpStatusCode.OK else HttpStatusCode.NotFound)
    }
}