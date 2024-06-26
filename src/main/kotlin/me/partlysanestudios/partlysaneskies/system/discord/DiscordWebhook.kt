package me.partlysanestudios.partlysaneskies.system.discord

import me.partlysanestudios.partlysaneskies.data.api.PostRequest
import me.partlysanestudios.partlysaneskies.data.api.RequestsManager
import me.partlysanestudios.partlysaneskies.utils.ChatUtils
import me.partlysanestudios.partlysaneskies.utils.SystemUtils
import java.net.URL


data class DiscordWebhook(
    var url: String,
    var content: String? = null,
    var embeds: List<DiscordEmbed>? = null
) {
    fun send() {
        if (content == null && embeds == null) {
            throw IllegalArgumentException("Either content or embeds must be set")
        }

        if (url.isEmpty()) {
            ChatUtils.sendClientMessage("Discord Webhook URL is not set")
            return
        }

        val urlObj = try {
            URL(url)
        } catch (expt: Exception) {
            ChatUtils.sendClientMessage("Discord Webhook URL is invalid")
            return
        }

        RequestsManager.newRequest(
            PostRequest(
                urlObj,
                {
                    if (!it.hasSucceeded()) {
                        ChatUtils.sendClientMessage("Discord Webhook failed to send\nCopied to clipboard")
                        SystemUtils.copyStringToClipboard(DiscordWebhookSerializer.serialize(this))
                        return@PostRequest
                    }
                    ChatUtils.sendClientMessage("Discord Webhook sent")
                },
                DiscordWebhookSerializer.serialize(this),
                false,
                false,
                true
            )
        )
    }
}
