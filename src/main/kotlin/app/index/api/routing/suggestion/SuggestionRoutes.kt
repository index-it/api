package app.index.api.routing.suggestion

import app.index.api.plugins.AuthenticationMethods
import app.index.api.routing.suggestion.routes.suggestionsRoute
import app.index.api.routing.suggestion.routes.templatesRoute
import io.ktor.resources.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Resource("/suggestions")
@Suppress("unused")
class SuggestionRoutes {
    @Resource("colors")
    class ColorsRoute(val parent: SuggestionRoutes = SuggestionRoutes())

    @Resource("list-names")
    class ListNamesRoute(val parent: SuggestionRoutes = SuggestionRoutes())

    @Resource("category-names")
    class CategoryNamesRoute(val parent: SuggestionRoutes = SuggestionRoutes())

    @Resource("item-names")
    class ItemNamesRoute(val parent: SuggestionRoutes = SuggestionRoutes())

    @Resource("task-names")
    class TaskNamesRoute(val parent: SuggestionRoutes = SuggestionRoutes())

    @Resource("templates")
    class TemplateRoute(val parent: SuggestionRoutes = SuggestionRoutes()) {
        @Resource("list")
        class ListRoute(val parent: TemplateRoute = TemplateRoute())

        @Resource("category")
        class CategoryRoute(val parent: TemplateRoute = TemplateRoute())

        @Resource("item")
        class ItemRoute(val parent: TemplateRoute = TemplateRoute())

        @Resource("task")
        class TaskRoute(val parent: TemplateRoute = TemplateRoute())
    }
}

fun Route.suggestionRoutes() {
    authenticate(AuthenticationMethods.USER_SESSION_AUTH) {
        suggestionsRoute()
        templatesRoute()
    }
}
