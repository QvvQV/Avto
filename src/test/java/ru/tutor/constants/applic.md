## base URL: <https://api.tutorplace.ru>

## Аутентификация и токены

* `POST /user/token/refresh` — обновление access token по refresh token
* `POST /login_token` *(в коде строка `login_token`)* — вход по одноразовому токену из query
* `GET /user/auth/{number}/{code?}` — авторизация/переход по номеру заказа и опциональному коду.
* `POST /user/login_check` — security login check, техническая ручка проверки логина.
* ~~`POST /user/reset` — fast reset, отдельная от `POST /reset` ручка сброса/восстановления.~~

> В вебе токены хранятся в `localStorage` (`token`, `refresh_token`) и отправляются как `Authorization: Bearer ...`. Клиент ещё включает `withCredentials: true`.

* ~~`POST /user/login` - вход в систему с email и паролем~~
* ~~`POST /user/logout` - завершает сессию пользователя.~~
* ~~`POST /register`  - создание нового пользователя.~~
* `POST /user/yalogin` - вход через Яндекс ID
* `POST /user/ya-register` - регистрация через Yandex ID
* ~~`GET /user` - проверка авторизации~~
* `POST /reset` - сброс пароля

## Профиль и “шапка”

* ~~`GET /user/profile` - по этой ручке идет получение всех флагов профиля клиента, определяющих сценарии его дальнейшего пользовательского опыта - участие в сплит-тестах, опросники при отписке, хроника начисления наград за заход на сайт и так далее.~~
* `POST /user/profile` - обновление данных или флагов профиля пользователя.
* ~~`PUT /user/edit/pass` - изменение пароля~~
* `POST / DELETE /user/photo` - загрузить или удалить аватар
* `PUT /user/has_sent_to_trackers` - отправляет click_id, utm-метки, ID устройства и прочие параметры аналитики
* ~~`PUT /user/edit/data` — изменение пользовательских данных.~~
* `PUT /user/edit/email` — изменение email.
* `GET /user/confirm/email` — подтверждение email.
* `GET /user/talk-me-link` — получение ссылки TalkMe.
* `PUT /user/edit/categories` — обновление пользовательских категорий.
* `POST /user/profile/deactivate` — деактивация профиля.

## Дашборды

* ~~`GET /user/dashboard/info`~~

Получает список доступных "дашбордов" — внутренних вспомогательно-развлекательных приложений, расширяющих пользовательский опыт.

По данной ручке список дашбордов приходит с названиями:

```json
["tutorDiet", "game", "telegram", "avitorg", "astrology", "matrix", "taro", "social-sputnik"]
```

Однако при вызове самих дашбордов используется уже другой нейминг, о чем будет написано ниже в статье.


## Каталог/категории

* ~~`GET /categories`~~

~~По этой ручке отдаются доступные категории контента. Пользователю мы выводим и сам список категорий, и "персонализированные" рекомендации курсов в рамках выбранных им категорий.~~


## Продукты/заказы/файлы

* ~~`GET /user/products` - отдает список продуктов, добавленных на конкретного пользователя~~
* ~~`GET /user/products/{id}` - в качестве id здесь идет айдишник заказа-продукта конкретного пользователя, эти id отдаются по запросу к `/user/products`.~~
* ~~`GET /user/products/all` - здесь мы получаем список вообще всех доступных на данный момент продуктов.~~
* ~~`GET /user/products/{orderNumber}/attached-files` - список доступных вложений у курса, если таковые есть~~
* `GET /user/products/{orderId}/files/{fileId}` - доступ к конкретному вложению курса *(на практике пока не получается проверить работу ручки, поведение и откуда берутся айдишники - будет уточнено позже)*
* `PUT /user/orders/{id}/completed_module/{number}` - отметка о прохождении урока, должно отправляться автоматически по факту просмотра 80% видео-урока. В ответе может приходить заголовок `x-show-nps-evaluation: 1`, по которому фронт решает, показывать ли NPS-опрос
* `POST /user/nps` — отправка пользовательской оценки NPS


Доп. действия по "карточкам":

* `POST /user/orders/product/{number}/hide` - удаляет продукт из добавленных по айдишнику заказа на этот продукт (note: сейчас есть фронтовый баг, что продукт еще и скрывается полностью из каталога)
* `POST /user/orders/pinned/{orderNumber}/{isPinned}` - закрепить добавленный курс, `isPinned` - булевый флаг, где 1 = закреплено, 0 = не закреплено
* `GET /user/{userId}/product/{productId}/quizzes` — список квизов продукта.
* `GET /user/{userId}/product/{productId}/quizzes/{quizSlug}` — детали квиза.
* `GET /user/{userId}/product/{productId}/quizzes/{quizSlug}/questions` — вопросы квиза.
* `GET /user/{userId}/quiz_answers/{quizSlug}` — ранее отправленные ответы пользователя по квизу.
* `POST /user/{userId}/quiz_answers` — отправка ответов пользователя.

# Подписки и отписки

* ~~`POST /user/orders/reactivate-premium-by-email` - вызывается, когда клиент возвращает премиум-подписку по призыву из email-рассылки~~
* `POST /user/orders/reactivate-premium` - реактивация премиума по кнопке в приложении
* `POST /user/orders/subscribe`  или `POST /user/orders/subscribe/{productId}` -  - оформление подписки
* `POST /user/orders/toggle_subscription/{productId}` - замена подписки
* `POST /user/orders/resubscribe` - возобновление подписки
* `POST /user/orders/unsubscribe` - отписка
* Переходы между тарифами
  `POST /user/orders/switch-premium`
  `POST /user/orders/switch-lite-lightweight`
* Восстановление подписок
  ~~`POST /user/orders/restore-premium`~~
  `POST /user/orders/restore-lite`
* Ребилл
    * `POST /user/orders/rebill/{productId}` — ручной ребилл по продукту.
    * `POST /user/orders/rebill_product/{productOrderNumber}` — ребилл/оплата по номеру заказа-продукта.
* ~~Пауза / снятие паузы~~
  ~~`PUT /user/orders/pause/{days}`~~
  ~~`PUT /user/orders/unpause`~~
* Апсейл
  ~~`GET /user/orders/upsale-content-invoice/{productOrderNumber}`~~
* ~~`GET /user/orders/{productOrderNumber}/upsell-variant` - вариант модального окна апсейлов (старое/новое)~~
* ~~`POST /user/orders/{productOrderNumber}/upsell-modal-opened` - открытие модального окна апсейлов~~
* ~~История платежей~~
  ~~`GET /user/orders/payments`~~
* Доступные платёжные системы
  `GET /available-payment-systems`
* ~~`GET /user/orders/bonus-access-info` — информация по бонусному доступу.~~
* `POST /user/orders/spend-cashback` — списание кэшбека.
* ~~`GET /user/orders/upsale-content-diamond/{orderNumber}` — апсейл за алмазы.~~
* `POST /user/orders/get-premium-gift` — получение премиум подарка. (нужно UI, без него тариф не активировать)
* ~~`POST /user/orders/store-feedback-unsubscribe/{orderNumber}` — сохранение отзыва при отписке.~~
* `POST /user/orders/pay-lifetime-tariff` — оплата lifetime-тарифа.
* `POST /user/orders/pay-lifetime-tariff-split` — оплата lifetime-тарифа через сплит платежа.
* `POST /user/orders/{orderNumber}/review/screenshot` — загрузка скриншота отзыва.
* `POST /user/orders/reactivate-premium-by-internal-mail` — реактивация премиума из внутренней почты.
* ~~`GET /user/orders/subscription/state` — состояние подписки.~~


## Refund tickets

* ~~`GET /user/refund-tickets` — список заявок/тикетов на возврат.~~
* ~~`POST /user/refund-tickets/create` — создание заявки на возврат.~~

## Сертификаты

* ~~`GET /user/certificate/generate` — генерация сертификата. Query: `name`, `orderNumber`.~~
* `GET /user/certificate/pdf` — получение сертификата в PDF. Query: `certificateId`, опционально `download`.
* `GET /user/certificate/png` — получение сертификата в PNG. Query: `certificateId`.


## Промокоды

* ~~`POST /user/promo-codes/apply` — применение промокода.~~
* `POST /user/promo-codes/activate-subscription` — активация подписки по промокоду.


## Видео-подкасты

* ~~`GET /user/videos` - список доступных видео в группе "Подкасты"~~
* ~~`GET /user/videos/{id}` - информация по конкретному видео~~
* ~~`GET /user/videos/watched/last` - если просмотр каких-то видео был оборван до завершения модуля, информация отдается по этой ручке и выводится фронтом в блоке "Продолжить просмотр" на главной~~
* `POST /user/videos/watched/create` - если мы не досмотрели видео и сбегаем со страницы, то фронт дергает эту ручку, передавая id заказа на продукт, по которому мы смотрели контент, номер начатого урока, а также временную отметку, на которой мы дропнули просмотр.
* ~~`GET /user/show-categories` - список категорий шоу~~

green-1
## Сторислайны / баннеры / тутсы

* ~~`GET /user/stories` - список доступных сторис~~
* ~~`GET /user/stories/{ulid}` - детали конкретной истории~~
* ~~`GET /user/banners/{slug}` - детали конкретного баннера (на данный момент баннеры в режиме сплит-теста, доступны не всем)~~
* ~~`POST /user/stories/screen` — фиксация события показа экрана сторис.~~
* ~~`POST /user/stories/view` — фиксация события просмотра стори.~~
* ~~`GET /user/tuts` — список тутсов с пагинацией. Query: `page`, `limit`, `sort`, `direction`.~~
* ~~`GET /user/tuts-categories` — список категорий тутсов с количеством доступных и непросмотренных тутсов.~~
* ~~`GET /user/tuts/category/{categoryId}/feed` — список тутсов в категории. Возвращает объекты со `slug` и `watched`.~~
* ~~`POST /user/tuts/{slug}/view` — запись факта просмотра тутса. Успешный ответ: `204 No Content`.~~
* `POST /user/tuts/-/likes` — добавить лайк к тутсу.
* `GET /user/tuts/{slug}/likes` — получить лайки тутса с пагинацией.
* `DELETE /user/tuts/{slug}/likes` — удалить реакции текущего пользователя у тутса.
* ~~`GET /user/tuts/random` — возвращает случайно перемешанный список доступных тутсов.~~
* ~~`GET - /user/tuts/random-list` - рандомный список тутсов~~

## Избранные курсы

* ~~`GET /user/favorite-products` - список продуктов, добавленных в избранное~~
* ~~`POST /user/favorite-products/{productId}` - запрос при добавлении курса в избранные по "сердечку".~~
* ~~`DELETE /user/favorite-products/{productId}` - изгнать продукт из перечня избранных~~


## Система заданий

* ~~`GET /user/tasks` - получить текущий список активных заданий~~
* ~~`POST /user/tasks/change/{id}` - заменить задание~~
* ~~`POST /user/tasks/finish/{id}` - отметить задание завершенным~~
* `POST /user/tasks/reset` - по контексту похоже, что сбросить текущий список "ежей" и выдать новые *(но по факту это так не работает)*.


## Геймификация

* ~~`GET /user/game-stats` - получение статистики профиля по геймификации: опыт, уровень, накопленная внутренняя валюта и т.д.~~
* ~~`GET /user/game-leader/rating` - получение списка лидеров в виде массивов по текущим дню, неделе и месяцу~~
* ~~`GET /user/achievements` - список всех достижений~~
* ~~`GET /user/achievements/last` - одним богам известно, что хотели сделать при помощи этой ручки, она отдает в ответе 6 ачивок по курсам, видимо, с которыми пользователь контактировал в последнее время. Не для вывода в профиль явно, т.к. в профиле выводятся другие ачивки.~~

Награды:

* ~~`GET /user/reward/list` - список подарков в профиле, которые еще не были забраны~~
* ~~`GET /user/reward/history` - история получения подарков с колеса~~
* ~~`POST /user/reward/open/{id}` - пометить подарок как полученный (если в профиле висят подарки, которые не были получены сразу)~~


## Колесо фортуны

* ~~`GET /user/game-wheel/rewards` - список всех доступных подарков за колесо~~
* ~~`GET /user/game-wheel/rewards-recently-won-all` - список недавних победителей и призов среди крутильщиков колеса для заманиловки, данные на фронте сейчас моковые, позже будут реальные~~
* ~~`POST /user/game-wheel/start-game` - инициация прокрутки колеса. Можно слать запросы бесконечно, пока не закончатся алмазы. Когда алмазы закончатся - можно продолжать спамить, но профита не будет, это баг.~~
* ~~`GET /user/game-wheel/timer-left-seconds` - возвращает кол-во секунд до бесплатной прокрутки.~~
* ~~`GET /user/game-wheel/rewards_groups` - награды~~


## Клуб TutorPlace (реферальная система для пользователей)

Без userId:

* ~~`DELETE /user/club/details` - скорее всего, удаление платежных реквизитов~~
* ~~`GET /user/club/member` - членство пользователя в клубе~~
* ~~`GET /user/club/payments` - история платежей пользователя~~
* ~~`GET /user/club/statistics` - статистика в клубе: кол-во приглашенных лично и косвенно + баланс~~
* ~~`POST /user/club/request-withdrawal`~~
* ~~`POST /user/club/ip-details` - эта ручка и следующая - добавление платежных реквизитов для вывода клубных денег~~
* ~~`POST /user/club/sz-details`~~
* `PUT user/club/v2/payouts/requisites` - реквизиты ИП

Клуб v2 (подтверждено на api1, авторизация user):

* `GET /user/club/v2/home/analytics` - аналитика главной клуба (totals + series по дням)
* `GET /user/club/v2/home/banners` - баннеры клуба
* `GET /user/club/v2/home/links` - ссылки участника клуба (+ фильтры dateFrom/dateTo)
* `GET /user/club/v2/home/news` - новости клуба (items + pagination)
* `GET /user/club/v2/home/news/{newsId}` - детальная новость (404 для отсутствующей)
* `GET /user/club/v2/analytics/links` - аналитика ссылок (level1/level2/level3)
* `GET /user/club/v2/analytics/monthly-income` - доход по уровням клуба
* `GET /user/club/v2/links/summary` - сводка по ссылкам (totals + breakdown)
* `GET /user/club/v2/referrals/{userId}` - детальная информация реферала (invitedBy + activity)
* `GET /user/club/v2/referrals/{userId}/referrals` - дерево рефералов реферала (404 для несуществующего userId)
* `GET /user/club/v2/payouts/summary` - сводка выплат (balance, canRequestWithdrawal)
* `GET /user/club/v2/referrals` - рефералы текущего пользователя (pagination + summary)

С userId в пути (тут мы начинаем понемножку уходить в рекурсию):

* ~~`GET /user/{userId}/club/referrals` - рефералы пользователя (userId)~~
* ~~`GET /user/{userId}/club/referrals/{referralId}` - конкретный реферал пользователя~~
* ~~`GET /user/{userId}/club/referrals/{referralId}/referrals` - рефералы этого реферала~~


## Инвайты/соцсети/связки

* `POST /user/yt/export-inviteLink`
* `POST /user/yandex/review-link`
* `POST /user/profile/link-with-telegram`
* `GET/POST /user/telegram/export-chat-inviteLink`
* `GET/POST /user/vk/export-inviteLink`

Все вышеуказанные ручки - часть интеграции со сторонними сервисами, они участвуют в проверке подписки пользователя на указанные соцсети/каналы и оставление отзыва на Яндексе.


## ТОП-10 курсов

* ~~`GET /user/product-positions/top10` - получение редакторского топ-10 самых-самых продуктов.~~

# Другие дашборды

Работают через единый фасад:

```
/user/entities/{orderId}/{dashboard}
```

Сами типы дашбордов:

* `diet-plan`
* `natal-chart`
* `taro`
* `game`
* `telegram`
* `matrix`

  (С матрицей дополнительно еще запрашивается <https://client.tutorplace.ru/matrix/prediction.csv> через GET)


**Поддерживаемые методы:**

GET — получить состояние

POST — обновить или создать

PUT — полная замена значения


* `POST /user/dashboard/analyze-dish` — анализ блюда по фото.
* `POST /user/dashboard/palmistry/photo` — загрузка фото для хиромантии.

# Загрузка рекламы из админки

Получение списка рекламы для модуля (урока)

Запрос:

```
GET /user/products/{productId}/modules/{moduleNumber}
```

# Дополнение

* `GET /user/products-app` — app-вариант списка продуктов.
* `GET /user/products/all-app` — app-вариант списка всех продуктов.
* ~~`GET /user/products/premiers` — список премьер.~~
* `GET /user/products/premiers-app` — app-вариант списка премьер.
* `GET /user/products/light-weight` — продукт премиум-99.
* `GET /user/products/lite-lightweight` — продукт лайт-29.
* ~~`GET /user/products/watching/now` — блок “сейчас смотрят”.~~
* `GET /user/products/watching/now-app` — app-вариант блока “сейчас смотрят”.
* `POST /user/products-notify` — подписка на уведомление о продукте/курсе.
* `DELETE /user/products-notify/{slug}` — отмена уведомления.
* `GET /user/products/{orderNumber}/modules/{number}/b-content` — b-content.
* ~~`GET /user/mini-courses` — список мини-курсов.~~
* `POST /user/mini-courses/{id}` — действие по мини-курсу.
* ~~`GET /user/mini-workouts` - список мини-тренировок~~
* ~~`GET /user/shows` - список шоу~~

## A/B и служебные события заказа

Служебные ручки для фиксации статусов и событий A/B-тестов.

* `POST /user/orders/{orderNumber}/ab-test-status`
* `POST /user/orders/{orderNumber}/ab-test-button-shown`
* `POST /user/orders/{orderNumber}/ab-test-video-progress`
* `PUT /user/orders/{orderNumber}/ab-test-focus`

## Поиск

* ~~`GET /user/search/history` — история поиска.~~
* ~~`POST /user/search/history` — запись поискового запроса в историю.~~
* `POST /user/search/notify` — уведомление/заявка по поиску.

## Авторы

* `GET /user/author/{id}` — информация об авторе.
* `POST /user/author/activate` — активация автора/связанного сценария.

## Онбординг

* `POST /user/onboarding/progress` — сохранение прогресса онбординга.
* `POST /user/onboarding/reward` — получение награды за онбординг.

## Курсы

* ~~`GET /user/courses/{productId}` — информация о курсе.~~
* ~~`GET /user/courses/{productId}/reviews` — отзывы курса.~~
* ~~`GET /user/courses/{productId}/recommendations` — рекомендации по курсу.~~
## Автовебинары

* `GET /user/{userId}/webinars` — список вебинаров пользователя.
* `GET /user/{userId}/webinars/{productId}/{startAt}` — данные конкретного вебинара.
* `GET /user/{userId}/webinars/{productId}/{startAt}/chat` — получить чат вебинара.
* `POST /user/{userId}/webinars/{productId}/{startAt}/chat` — отправить сообщение в чат вебинара.