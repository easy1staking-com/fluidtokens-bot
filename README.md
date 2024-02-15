# Fluid Tokens BOT

## Run BOT w/ Docker

In order to run this bot you need a `mainnet` account on blockfrost, and a 24-words seed cardano wallet with a bunch of ada.

Create and env file like the following:
```
WALLET_MNEMONIC=lorem ipsum .. yada yada yada
BLOCKFROST_KEY=mainnet123sbffoehferuif
```

Then run

`docker run --rm --env-file .env -d speedwing/fluidtokens-bot:latest`

When no expired rents are found logs will look like...

```bash
2024-02-14T14:42:18.728Z  INFO 1 --- [   scheduling-1] c.f.nft.borrow.service.ReturnNftJob      : Running
2024-02-14T14:42:19.682Z  INFO 1 --- [   scheduling-1] c.f.nft.borrow.service.ReturnNftJob      : found 0 expired rents
2024-02-14T14:42:19.683Z  INFO 1 --- [   scheduling-1] c.f.nft.borrow.service.ReturnNftJob      : Completed
```

While if expired rents are found, it'll look something like...

```bash
2024-02-14T16:25:20.030Z  INFO 1 --- [   scheduling-1] c.f.nft.borrow.service.ReturnNftJob      : Running
2024-02-14T16:25:20.937Z  INFO 1 --- [   scheduling-1] c.f.nft.borrow.service.ReturnNftJob      : found 2 expired rents
2024-02-14T16:25:23.480Z  INFO 1 --- [   scheduling-1] c.b.c.client.quicktx.QuickTxBuilder      : [Submitted] Tx: 90154f6d9fc13acf0f95610ce27d80dbdae4bd9850c3fae24af59534b5ced550
2024-02-14T16:25:23.664Z  INFO 1 --- [   scheduling-1] c.b.c.client.quicktx.QuickTxBuilder      : [Pending] Tx: 90154f6d9fc13acf0f95610ce27d80dbdae4bd9850c3fae24af59534b5ced550
2024-02-14T16:25:25.754Z  INFO 1 --- [   scheduling-1] c.b.c.client.quicktx.QuickTxBuilder      : [Pending] Tx: 90154f6d9fc13acf0f95610ce27d80dbdae4bd9850c3fae24af59534b5ced550
2024-02-14T16:25:27.833Z  INFO 1 --- [   scheduling-1] c.b.c.client.quicktx.QuickTxBuilder      : [Pending] Tx: 90154f6d9fc13acf0f95610ce27d80dbdae4bd9850c3fae24af59534b5ced550
2024-02-14T16:25:29.890Z  INFO 1 --- [   scheduling-1] c.b.c.client.quicktx.QuickTxBuilder      : [Confirmed] Tx: 90154f6d9fc13acf0f95610ce27d80dbdae4bd9850c3fae24af59534b5ced550
2024-02-14T16:25:29.890Z  INFO 1 --- [   scheduling-1] c.f.nft.borrow.service.ReturnNftJob      : Completed
```

## Resources

This BOT has developed by Giovanni, SPO of [EASY1 Stake Pool](https://easy1staking.com), using the following languages and frameworks:

1. Java JDK 17
2. Spring Boot 3.1.5
3. [Cardano Client Lib 0.5.0](https://github.com/bloxbean/cardano-client-lib)

## Support 

Like what you're seeing? Support me by delegating to the [EASY1 Stake Pool](https://pool.pm/20df8645abddf09403ba2656cda7da2cd163973a5e439c6e43dcbea9)
