import android.content.Context
import android.media.MediaPlayer

class SoundPlayer(context: Context, soundResId: Int) {

    private val mediaPlayer: MediaPlayer = MediaPlayer.create(context, soundResId)

    init {
        // 소리를 최대 볼륨으로 설정
        mediaPlayer.setVolume(1.0f, 1.0f) // 왼쪽 채널과 오른쪽 채널 모두 최대 볼륨으로 설정
    }

    fun playSound() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    fun release() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }
}
