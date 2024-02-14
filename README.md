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
