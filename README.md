# ArbitrageSimulator

## What it is
- This code simulates automated currency trading and spotting arbitrage opportunity
- This code has 4 components, 1 executor(client) and 3 liquidity providers(trading platform)

## How it works
- This code utilizes java's socket programming and have client subscribe to data from liquidity providers and send trade request to liquidity provider
- Each liquidity provider provides ticker every second and has different ttl for the tickers
- Client will use a trading strategy to get the liquidity provider to buy and sell from to gain profit

## Future improvements
- Code can be made to adhere better to OOP principles, certain code can still be abstracted out. Liquidity provider servers can have an interface to be implemented from
- Expand so that it can include multiple currencies
- Improve trading strategy to take ttl into account when making trades
- Improve trading strategy to return multiple currency pairs at a time

ps. a script is included to run the code
