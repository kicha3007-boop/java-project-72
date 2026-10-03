@import hexlet.code.util.NamedRoutes

@template.layout.page(
  content = @`
    <h1 class="mb-4 text-3xl font-bold">Что-то пошло не так</h1>
    <p class="mb-6 text-gray-600">На сервере произошла ошибка. Попробуйте ещё раз чуть позже.</p>
    <a class="text-blue-600 hover:underline" href="${NamedRoutes.rootPath()}">На главную</a>
  `
)
