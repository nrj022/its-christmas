package com.zcard.domain.exception

class CardNotExportedException(cardId: Long): Exception("Card with id $cardId is not exported")