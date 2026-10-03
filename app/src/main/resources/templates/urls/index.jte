@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@import hexlet.code.util.TextUtils
@param UrlsPage page

@template.layout.page(
  page = page,
  content = @`
    <h1 class="mb-6 text-3xl font-bold">Сайты</h1>
    <div class="overflow-x-auto">
      <table data-test="urls" class="w-full border-collapse text-left">
        <thead>
          <tr class="border-b border-gray-300">
            <th class="px-4 py-2 font-semibold">ID</th>
            <th class="px-4 py-2 font-semibold">Имя</th>
            <th class="px-4 py-2 font-semibold">Последняя проверка</th>
            <th class="px-4 py-2 font-semibold">Код ответа</th>
          </tr>
        </thead>
        <tbody>
          @for(var url : page.getUrls())
            !{var check = page.getLatestCheck(url);}
            <tr class="border-b border-gray-100 odd:bg-gray-50">
              <td class="px-4 py-2">${url.getId()}</td>
              <td class="px-4 py-2">
                <a class="text-blue-600 hover:underline" href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
              </td>
              <td class="px-4 py-2">${check == null ? "" : TextUtils.formatDate(check.getCreatedAt())}</td>
              <td class="px-4 py-2">${check == null ? "" : String.valueOf(check.getStatusCode())}</td>
            </tr>
          @endfor
        </tbody>
      </table>
    </div>
  `
)
