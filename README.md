# wallet-service

  **WALLET-SERVICE** - это консольное приложение, разработанное на основе Java Core и Java Collections. В нем реализован следующий функционал: регистрация, авторизация, просмотр баланса, пополнение счета, снятие денег, просмотр истории транзакций текущего игрока, а также просмотр логов всех игроков (только для админа). Проект также включает в себя модульное тестирование с использованием JUnit5 (AssertJ, Mockito, Test-containers) и Javadoc.
 
  Для запуска приложения перейдите в пакет org.example.walletservice, где находится класс WalletServiceApplication - с него начинается запуск приложения, а так же запустите БД PostgreSQL в контейнере Docker, введя команду docker-compose up -d в корне проекта. При запуске приложения у вас уже будет создан администратор (username: admin, password: admin). Он имеет дополнительный функционал, доступный после авторизации: просмотр аудитов всех пользователей или аудит конкретного пользователя с указанием его "username".
  
  После запуска приложения в консоли отобразится меню регистрации/авторизации. После первого запуска программы мы можем авторизоваться только под ролью "admin". Если нужна роль "user", необходимо пройти регистрацию и затем авторизироваться под данными, которые были использованы при регистрации.
  
  После успешной авторизации у вас будет доступ к элементам интерфейса авторизованного пользователя: баланс, кредит, дебет, просмотр истории транзакций и выход из учетной записи. Если вы вошли как администратор, дополнительно отображаются опции просмотра логов всех игроков или логов конкретного пользователя. Логи содержат дату создания, имя пользователя, номер транзакции, выполняемую операцию и оставшуюся сумму на счету после совершения операции. Можно переключаться между аккаунтами игроков.

  При операциях Credit / Debit необходим ввод суммы и токен транзакции. В случае, если токен транзакции не уникален, то транзакция не будет завершена, а так же если сумма снятия средст больше суммы средства на счета, то транзакция так же не будет завершена.

	В данной версии проекта применялись следующие технологии:
        -- Java 17
        -- JDBC
        -- PostgreSQL
        -- Liquibase
        -- Lombok
        -- Тестирование - JUnit5, Mockito, AssertJ, Test-containers
