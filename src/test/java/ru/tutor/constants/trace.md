# Trace API-обходов (dev1)

Формат записи: каждый вызов = блок `##`. Пути шаблонизированы (`{userId}`).
auth: `user` = `Authorization: Bearer <JWT>` (получен `POST /user/login`), `admin` = `Cookie: ADMINSESSID=<sid>` (получен `POST /admin/login`), `none` = без авторизации.
base URL: <https://api1.ttplace.online>. Применение: `contract` (GET, безопасный) | `mutation` (POST/PUT/DELETE).

---

## Батч 1 (2026-10-06): club v2 — home / analytics / links / referrals

### 1. GET /user/club/v2/home/analytics
* Auth: user
* Query: – (в ответе фильтры: dateFrom/dateTo nullable, step, level, linkId)
* -> 200 | application/json
* Ключевые поля: `filters{...}`, `totals{clicks,newSubscriptions,subscriptions,unsubscribed,paymentError,income}`, `series.clicks[{date,value}]` (по дням)
* Динамика: –
* Применение: contract

### 2. GET /user/club/v2/home/banners
* Auth: user
* Query: –
* -> 200 | application/json
* Ключевые поля: `items[{id,title,description,image,mobileImage,link}]`
* Динамика: –
* Применение: contract (факт: items могут быть пустые)

### 3. GET /user/club/v2/home/links
* Auth: user
* Query: – (в ответе фильтры dateFrom=dateTo=today)
* -> 200 | application/json
* Ключевые поля: `filters{dateFrom,dateTo}`, `items[{id,name,url,clicks,trial,premium,paymentError,unsubscribed,income}]`; `id` — id ссылки, `url` — `https://dev1.clubtp.ru/club/link/{hash}`
* Динамика: `item.id` → для links/summary и analytics/links
* Применение: contract

### 4. GET /user/club/v2/home/news
* Auth: user
* Query: –
* -> 200 | application/json
* Ключевые поля: `items[]`, `pagination{count,total,per_page,page,pages}`
* Динамика: –
* Применение: contract (items допускают пустоту)

### 5. GET /user/club/v2/home/news/{newsId}
* Auth: user
* Query: –
* -> 404 | application/json для несуществующего id («Новость не найдена», ClubNewsService:49)
* Динамика: newsId из шага 4 (сейчас новостей нет → детальная ручка недоступна, 404)
* Применение: contract (негативно: 404 для отсутствующего id)

### 6. GET /user/club/v2/analytics/links
* Auth: user
* Query: – (в ответе фильтры dateFrom/dateTo/level/linkId)
* -> 200 | application/json
* Ключевые поля: `filters`, `items[{id,name,product{id,name},referralsTotal,level1,level2,level3,income}]`, `pagination{count,total,per_page,page,pages}`
* Динамика: `item.id` (linkId)
* Применение: contract

### 7. GET /user/club/v2/analytics/monthly-income
* Auth: user
* Query: –
* -> 200 | application/json
* Ключевые поля: `filters{dateFrom,dateTo,level,linkId}`, `items[{level,label,rate,active,incomePerMonth}]` (уровни 1/2/3), `totals{active,incomePerMonth}`
* Динамика: –
* Применение: contract

### 8. GET /user/club/v2/links/summary
* Auth: user
* Query: – (в ответе фильтры dateFrom/dateTo/step)
* -> 200 | application/json
* Ключевые поля: `filters`, `totals{linksCount,clicks,activeReferrals,activePremiums,income}`, `breakdown{trial,lite,premium,lifetime,paymentError,unsubscribed}`, `series{clicks[{date,value}],income[],activePremiums[]}`
* Динамика: –
* Применение: contract

### 9. GET /user/club/v2/referrals/{userId}
* Auth: user
* Query: –
* -> 200 | application/json; userId = `items[].userId` из `GET /user/club/v2/referrals`
* Ключевые поля: `{userId,name,avatarUrl,level,invitedBy{type,label,userId},subscriptionStatus{code,label,isActive,stateLabel},incomePerMonth,incomeOneTime,link{...},activity{lastLoginAt,registeredAt,addedCoursesCount,completedLessonsCount}}`
* Динамика: userId — из списка рефералов
* Применение: contract

### 10. GET /user/club/v2/referrals/{userId}/referrals
* Auth: user
* Query: –
* -> 200 | application/json; `{items[],pagination{count,total,per_page,page,pages}}`
* Динамика: userId — из списка рефералов
* Применение: contract

### 11. GET /user/club/v2/referrals/{nonexistentUserId}
* Auth: user
* Query: – (id=999999999)
* -> 404 | application/json («Реферал не найден», ClubReferralController:126)
* Динамика: –
* Применение: contract (негативно: 404)

### 12. GET /user/club/v2/payouts/summary
* Auth: user
* Query: –
* -> 200 | application/json
* Ключевые поля: `{balance,availableForWithdrawal,shareBonusExcludedAmount,minWithdrawalAmount,clubMembership,profileValid,canRequestWithdrawal}`
* Динамика: –
* Применение: contract (шаги уже есть в ClubSteps)