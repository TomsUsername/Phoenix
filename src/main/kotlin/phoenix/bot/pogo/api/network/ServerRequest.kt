package phoenix.bot.pogo.api.network

import com.google.protobuf.ByteString
import phoenix.bot.pogo.api.PoGoApi

interface ServerRequest {
    fun build(poGoApi: PoGoApi): com.google.protobuf.GeneratedMessage
    fun getRequestType(): POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType

    val response: com.google.protobuf.GeneratedMessage

    fun getBuilder(): com.google.protobuf.MessageOrBuilder

    fun setResponse(payload: ByteString)
}