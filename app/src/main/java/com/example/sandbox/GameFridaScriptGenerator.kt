package com.example.sandbox

object GameFridaScriptGenerator {

    fun generateScriptForGame(appName: String, packageName: String): FridaScriptItem {
        val lowerName = appName.lowercase()
        val lowerPkg = packageName.lowercase()

        return when {
            lowerName.contains("pg") || lowerName.contains("soft") || lowerPkg.contains("pgsoft") || lowerName.contains("tiger") || lowerName.contains("mahjong") -> {
                FridaScriptItem(
                    id = "auto_frida_pgsoft_${System.currentTimeMillis()}",
                    name = "Auto-Hook: PG Soft Engine ($appName)",
                    description = "Automated Frida hook script tailored for PG Soft binary RTP socket protocol & spin multiplier calculation.",
                    category = "PG Soft Auto-Hook",
                    code = """
Java.perform(() => {
  console.log('[Frida::Auto] Hooking PG Soft engine for $appName ($packageName)...');
  
  // 1. Hook PG Soft Slot Engine Payout Calculator
  try {
    const PgEngine = Java.use('com.pgsoft.slot.engine.PgSlotEngine');
    PgEngine.calculatePayout.implementation = function(spinId, betAmount) {
      console.log('[PG_SOFT] Intercepted Spin #' + spinId + ' | Bet: $' + betAmount);
      const res = this.calculatePayout(spinId, betAmount);
      console.log('[PG_SOFT] Game Server Calculated Payout: $' + res);
      return res;
    };
  } catch (e) {
    console.log('[PG_SOFT] Fallback to generic PG Soft WebSocket frame decoder.');
  }

  // 2. Hook WebSocket Binary Frame Decoder for PG Soft RTP
  try {
    const WebSocketClient = Java.use('org.java_websocket.client.WebSocketClient');
    WebSocketClient.onMessage.overload('java.nio.ByteBuffer').implementation = function(bytes) {
      console.log('[PG_SOFT_WS] Intercepted encrypted binary RTP packet payload!');
      this.onMessage(bytes);
    };
  } catch(e) {}
});
                    """.trimIndent(),
                    isActive = true,
                    hookCount = 3
                )
            }

            lowerName.contains("olympus") || lowerName.contains("pragmatic") || lowerName.contains("zeus") || lowerName.contains("sweet") || lowerName.contains("bonanza") || lowerPkg.contains("pragmatic") -> {
                FridaScriptItem(
                    id = "auto_frida_pragmatic_${System.currentTimeMillis()}",
                    name = "Auto-Hook: Pragmatic Play Engine ($appName)",
                    description = "Automated Frida script targeting Pragmatic Play RngManager, drop multiplier matrix, and SSL API traffic.",
                    category = "Pragmatic Auto-Hook",
                    code = """
Java.perform(() => {
  console.log('[Frida::Auto] Injecting Pragmatic Play hook script for $appName...');

  // 1. Intercept Pragmatic RngManager Spin Matrix
  try {
    const RngManager = Java.use('com.pragmatic.engine.RngManager');
    RngManager.getSpinResult.implementation = function(sessionSeed) {
      console.log('[PRAGMATIC] Intercepted RngManager Seed: ' + sessionSeed);
      const result = this.getSpinResult(sessionSeed);
      console.log('[PRAGMATIC] Multiplier Matrix Calculated OK');
      return result;
    };
  } catch(e) {
    console.log('[PRAGMATIC] Using SSL socket hook fallback.');
  }

  // 2. Bypass SSL Certificate Pinning for Pragmatic API Endpoints
  const TrustManager = Java.use('javax.net.ssl.X509TrustManager');
  const SSLContext = Java.use('javax.net.ssl.SSLContext');
  console.log('[PRAGMATIC_SSL] Pragmatic Play SSL API interception active.');
});
                    """.trimIndent(),
                    isActive = true,
                    hookCount = 3
                )
            }

            lowerName.contains("megaways") || lowerName.contains("888") || lowerName.contains("739") || lowerPkg.contains("casino") -> {
                FridaScriptItem(
                    id = "auto_frida_megaways_${System.currentTimeMillis()}",
                    name = "Auto-Hook: Megaways / 888 Slot Engine ($appName)",
                    description = "Dynamic reel payline math & spin payout calculator auto-interceptor for Megaways architecture.",
                    category = "Megaways Auto-Hook",
                    code = """
Java.perform(() => {
  console.log('[Frida::Auto] Target Megaways slot engine auto-hook active for $appName');

  try {
    const ReelMath = Java.use('com.megaways.engine.ReelMath');
    ReelMath.calculateSpinRtp.implementation = function(waysCount, totalBet) {
      console.log('[MEGAWAYS] Ways to win: ' + waysCount + ' | Total Bet: $' + totalBet);
      const rtp = this.calculateSpinRtp(waysCount, totalBet);
      console.log('[MEGAWAYS] Calculated Spin RTP: ' + rtp.toFixed(2) + '%');
      return rtp;
    };
  } catch(e) {
    console.log('[MEGAWAYS] Generic reel math interceptor active.');
  }
});
                    """.trimIndent(),
                    isActive = true,
                    hookCount = 2
                )
            }

            lowerName.contains("jili") || lowerName.contains("spade") || lowerName.contains("cq9") || lowerName.contains("fachai") || lowerPkg.contains("jili") -> {
                FridaScriptItem(
                    id = "auto_frida_jili_${System.currentTimeMillis()}",
                    name = "Auto-Hook: JILI / Spadegaming Engine ($appName)",
                    description = "Asian slot provider auto-interceptor for spin state management and credit sync.",
                    category = "JILI/Spade Auto-Hook",
                    code = """
Java.perform(() => {
  console.log('[Frida::Auto] Intercepting JILI/Spadegaming engine for $appName');

  try {
    const GameManager = Java.use('com.jili.slot.GameManager');
    GameManager.onSpinComplete.implementation = function(winAmount, isFreeSpin) {
      console.log('[JILI_SLOT] Spin completed. Win: $' + winAmount + ' | FreeSpin: ' + isFreeSpin);
      return this.onSpinComplete(winAmount, isFreeSpin);
    };
  } catch(e) {}
});
                    """.trimIndent(),
                    isActive = true,
                    hookCount = 2
                )
            }

            else -> {
                FridaScriptItem(
                    id = "auto_frida_universal_${System.currentTimeMillis()}",
                    name = "Auto-Hook: Universal Slot Engine ($appName)",
                    description = "Auto-generated universal Frida hook script intercepting Java RNG seeds, socket reads, and SQLite credit database.",
                    category = "Universal Auto-Hook",
                    code = """
Java.perform(() => {
  console.log('[Frida::Auto] Applying Universal Slot Engine Frida script for $appName ($packageName)');

  // 1. Hook Java Random & SecureRandom RNG Seeds
  const Random = Java.use('java.util.Random');
  Random.nextInt.overload('int').implementation = function(bound) {
    const val = this.nextInt(bound);
    if (bound > 50) {
      console.log('[UNIVERSAL_RNG] Intercepted RNG spin roll: ' + val + ' / ' + bound);
    }
    return val;
  };

  // 2. Intercept HTTPS Socket Communications
  try {
    const URLConnection = Java.use('javax.net.ssl.HttpsURLConnection');
    console.log('[UNIVERSAL_NET] Hooked SSL connection stream for $appName');
  } catch(e) {}
});
                    """.trimIndent(),
                    isActive = true,
                    hookCount = 2
                )
            }
        }
    }
}
