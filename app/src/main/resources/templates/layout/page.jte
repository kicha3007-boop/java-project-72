@import hexlet.code.dto.BasePage
@import hexlet.code.util.NamedRoutes
@import gg.jte.Content
@param Content content
@param BasePage page = null

<!DOCTYPE html>
<html lang="ru">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Анализатор страниц</title>
    <link href="/css/main.css" rel="stylesheet">
  </head>

  <body class="flex min-h-screen flex-col bg-white text-gray-900">
    <nav class="bg-gray-900">
      <div class="mx-auto flex max-w-6xl flex-wrap items-center gap-6 px-4 py-3">
        <a class="text-lg font-medium text-white" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>
        <a class="text-sm text-gray-300 hover:text-white" href="${NamedRoutes.rootPath()}">Главная</a>
        <a class="text-sm text-gray-300 hover:text-white" href="${NamedRoutes.urlsPath()}">Сайты</a>
      </div>
    </nav>

    <main class="flex-1">
      @if(page != null && page.getFlash() != null)
        !{var flashClass = "success".equals(page.getFlashType())
            ? "border-green-200 bg-green-50 text-green-800"
            : "border-red-200 bg-red-50 text-red-800";}
        <div class="border-b px-4 py-3 ${flashClass}" role="alert">
          <p class="mx-auto max-w-6xl">${page.getFlash()}</p>
        </div>
      @endif
      <div class="mx-auto w-full max-w-6xl px-4 py-8">
        ${content}
      </div>
    </main>

    <footer class="border-t border-gray-200 py-4">
      <div class="mx-auto max-w-6xl px-4 text-center text-sm text-gray-600">
        created by
        <a class="text-blue-600 hover:underline" href="https://ru.hexlet.io" target="_blank">Hexlet</a>
      </div>
    </footer>
  </body>
</html>
