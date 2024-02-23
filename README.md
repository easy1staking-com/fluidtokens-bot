# Fluid Tokens BOT

## Datum Type

``` 
type Datum {
  //The owner
  owner_address: Address,
  //The daily price policy and asset
  daily_rent_policy: PolicyId,
  daily_rent_asset: AssetName,
  //daily amount -> This is daily rent for NFT while for ADA it is derived from APY: 5% APY is 136000 lovelace as daily rent while 20% is 548000 lovelace daily rent
  daily_rent_amount: Int,
  pool_policy: PolicyId,
  pool_asset: AssetName,
  //If it's a pool the starting value of the utxo otherwise is the NFT
  //1n for NFT, amount for tokens
  pool_amount: Int,
  //this is because decimals are harder for nft divider is 1 but for ADA divider is 1000000 (1M)
  pool_divider: Int,
  //the current address that is the tenant, while available the same as the owner
  tenant_address: Address,
  deadline_date: Int,
  //end of active rent -> Batchers should be able to send it back to owner
  //not possible to rent after this date
  expiration_offer: Int,
  //for fees
  fluid_address: Address,
  //this is a millesimal percentage so fee_pergentage=10=1% fee
  fee_percentage: Int,
  //min days
  min_days: Int,
  //if 1 daily rent if 5 I can only rent multiple of 5 days
  multiplier: Int,
}
```

## Run BOT w/ Docker

In order to run this bot you need a `mainnet` account on blockfrost, and a 24-words seed cardano wallet with a bunch of ada.

Create and env file like the following:
```
WALLET_MNEMONIC=lorem ipsum .. yada yada yada
BLOCKFROST_KEY=mainnet123sbffoehferuif
```

Then run

`docker run --rm --env-file .env -d fluidtokens/fluidtokens-bot:latest`

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

This BOT has developed using the following languages and frameworks:

1. Java JDK 17
2. Spring Boot 3.1.5
3. [Cardano Client Lib 0.5.1](https://github.com/bloxbean/cardano-client-lib)
4. [Yaci Store](https://github.com/bloxbean/yaci-store)

## Cardano Client Lib - Java

The Java Cardano Client Lib is growing fast and in this project you can see how easy it is to write sophisticated,
production grade, code to achieve complex data indexing.

Wants to find out more? Join the Blox Bean Discord server and join a vibrant committed java developer community. 

Thanks

## Kudos

Hi! This is Giovanni from [EASY1 Stake Pool](https://pool.pm/20df8645abddf09403ba2656cda7da2cd163973a5e439c6e43dcbea9)
I'm very happy to have contributed to the creation of this project for Fluidtokens.

If you find this project interesting and wants to know more, follow me on github or [twitter](https://twitter.com/CryptoJoe101) 
