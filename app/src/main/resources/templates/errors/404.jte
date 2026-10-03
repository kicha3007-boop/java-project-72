@import hexlet.code.util.NamedRoutes

@template.layout.page(
  content = @`
    <h1 class="mb-4 text-3xl font-bold">Страница не найдена</h1>
    <p class="mb-6 text-gray-600">Такой страницы нет. Проверьте адрес или вернитесь на главную.</p>
    <a class="text-blue-600 hover:underline" href="${NamedRoutes.rootPath()}">На главную</a>
  `
)
