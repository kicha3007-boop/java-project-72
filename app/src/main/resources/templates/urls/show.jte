@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.TextUtils
@param UrlPage page

!{var url = page.getUrl();}

@template.layout.page(
  page = page,
  content = @`
    <h1 class="mb-6 text-3xl font-bold">Сайт: ${url.getName()}</h1>
    <div class="overflow-x-auto">
      <table data-test="url" class="w-full border-collapse text-left">
        <tbody>
          <tr class="border-b border-gray-100">
            <td class="w-48 px-4 py-2 font-semibold">ID</td>
            <td class="px-4 py-2">${url.getId()}</td>
          </tr>
          <tr class="border-b border-gray-100">
            <td class="px-4 py-2 font-semibold">Имя</td>
            <td class="px-4 py-2">${url.getName()}</td>
          </tr>
          <tr class="border-b border-gray-100">
            <td class="px-4 py-2 font-semibold">Дата создания</td>
            <td class="px-4 py-2">${TextUtils.formatDate(url.getCreatedAt())}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <h2 class="mt-10 mb-4 text-2xl font-bold">Проверки</h2>
    <form method="post" action="${NamedRoutes.urlChecksPath(url.getId())}">
      <input type="submit"
             class="cursor-pointer rounded bg-blue-600 px-6 py-2 font-medium text-white hover:bg-blue-700"
             value="Запустить проверку">
    </form>

    <div class="mt-6 overflow-x-auto">
      <table data-test="checks" class="w-full border-collapse text-left">
        <thead>
          <tr class="border-b border-gray-300">
            <th class="px-4 py-2 font-semibold">ID</th>
            <th class="px-4 py-2 font-semibold">Код ответа</th>
            <th class="px-4 py-2 font-semibold">h1</th>
            <th class="px-4 py-2 font-semibold">title</th>
            <th class="px-4 py-2 font-semibold">description</th>
            <th class="px-4 py-2 font-semibold">Дата создания</th>
          </tr>
        </thead>
        <tbody>
          @for(var check : page.getChecks())
            <tr class="border-b border-gray-100 odd:bg-gray-50">
              <td class="px-4 py-2">${check.getId()}</td>
              <td class="px-4 py-2">${check.getStatusCode()}</td>
              <td class="px-4 py-2">${TextUtils.truncate(check.getH1())}</td>
              <td class="px-4 py-2">${TextUtils.truncate(check.getTitle())}</td>
              <td class="px-4 py-2">${TextUtils.truncate(check.getDescription())}</td>
              <td class="px-4 py-2">${TextUtils.formatDate(check.getCreatedAt())}</td>
            </tr>
          @endfor
        </tbody>
      </table>
    </div>
  `
)
