import {
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View,
  useWindowDimensions
} from "react-native";
import { BatteryCard } from "../components/BatteryCard";
import { MetricCard } from "../components/MetricCard";
import { SensorTile } from "../components/SensorTile";
import { colors } from "../theme/colors";
import { CrawlerTelemetry, Direction } from "../types/crawler";

interface Props {
  telemetry: CrawlerTelemetry;
  onManualStart: (direction: Direction) => void;
  onManualStop: () => void;
  onAutoStart: (direction: Direction) => void;
  onStop: () => void;
  onToggleStandby: () => void;
}

const stateLabels: Record<CrawlerTelemetry["state"], string> = {
  WAITING: "Ожидание",
  STOP: "Стоп",
  MOVING: "Движение",
  EXPOSURE: "Экспозиция",
  CALIBRATION: "Калибровка"
};

export function DashboardScreen({
  telemetry,
  onManualStart,
  onManualStop,
  onAutoStart,
  onStop,
  onToggleStandby
}: Props) {
  const { width } = useWindowDimensions();
  const compact = width < 700;
  const manualAllowed = telemetry.state === "WAITING" || telemetry.state === "STOP";
  const autoAllowed = telemetry.state === "STOP";

  const movementButton = (label: string, direction: Direction) => (
    <Pressable
      disabled={!manualAllowed}
      onPressIn={() => onManualStart(direction)}
      onPressOut={onManualStop}
      style={({ pressed }) => [
        styles.holdButton,
        !manualAllowed && styles.disabled,
        pressed && manualAllowed && styles.holdPressed
      ]}
    >
      <Text style={styles.holdArrow}>{direction === "FORWARD" ? "↑" : "↓"}</Text>
      <Text style={styles.holdTitle}>{label}</Text>
      <Text style={styles.holdHint}>УДЕРЖИВАТЬ</Text>
    </Pressable>
  );

  return (
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.hero}>
        <View>
          <Text style={styles.eyebrow}>СОСТОЯНИЕ КРОУЛЕРА</Text>
          <Text style={styles.state}>{stateLabels[telemetry.state]}</Text>
          <Text style={styles.serial}>
            {telemetry.name} · {telemetry.serialNumber}
          </Text>
        </View>
        <View style={[styles.connection, !telemetry.connected && styles.connectionOff]}>
          <Text style={styles.connectionTitle}>
            {telemetry.connected ? "BLE подключено" : "Связь потеряна"}
          </Text>
          <Text style={styles.connectionMeta}>{telemetry.bleRssiDbm} dBm</Text>
        </View>
      </View>

      <View style={styles.grid}>
        <MetricCard label="Расстояние" value={`${telemetry.distanceMeters.toFixed(1)} м`} detail="от исходной позиции" />
        <MetricCard label="Крен" value={`${telemetry.rollDeg.toFixed(1)}°`} />
        <MetricCard label="Тангаж" value={`${telemetry.pitchDeg.toFixed(1)}°`} />
        <MetricCard label="Торможение" value={`${telemetry.brakePercent}%`} />
      </View>

      <View style={styles.batteryRow}>
        <BatteryCard title="Ходовой аккумулятор" battery={telemetry.driveBattery} />
        <BatteryCard title="Рентгеновский аккумулятор" battery={telemetry.xrayBattery} />
      </View>

      <Text style={styles.sectionTitle}>Датчики безопасности</Text>
      <View style={styles.grid}>
        <SensorTile label="Начало трубы" active={telemetry.sensors.pipeStart} />
        <SensorTile label="Конец трубы" active={telemetry.sensors.pipeEnd} />
        <SensorTile label="Вода" active={telemetry.sensors.water} />
        <SensorTile label="Препятствие" active={telemetry.sensors.obstacle} />
      </View>

      <Text style={styles.sectionTitle}>Управление движением</Text>
      <View style={[styles.controlPanel, compact && styles.controlPanelCompact]}>
        <View style={styles.manualColumn}>
          <Text style={styles.controlCaption}>РУЧНОЕ ДВИЖЕНИЕ</Text>
          <View style={styles.manualRow}>
            {movementButton("Вперёд", "FORWARD")}
            {movementButton("Назад", "BACKWARD")}
          </View>
        </View>

        <View style={styles.actionColumn}>
          <Pressable
            disabled={!autoAllowed}
            onPress={() => onAutoStart("FORWARD")}
            style={[styles.actionButton, !autoAllowed && styles.disabled]}
          >
            <Text style={styles.actionText}>Авто вперёд</Text>
          </Pressable>
          <Pressable
            disabled={!autoAllowed}
            onPress={() => onAutoStart("BACKWARD")}
            style={[styles.actionButton, !autoAllowed && styles.disabled]}
          >
            <Text style={styles.actionText}>Авто назад</Text>
          </Pressable>
          <Pressable onPress={onToggleStandby} style={styles.secondaryButton}>
            <Text style={styles.secondaryText}>
              {telemetry.state === "WAITING" ? "Перейти в Стоп" : "Режим ожидания"}
            </Text>
          </Pressable>
        </View>

        <Pressable onPress={onStop} style={styles.stopButton}>
          <Text style={styles.stopLabel}>STOP</Text>
          <Text style={styles.stopHint}>Остановить</Text>
        </Pressable>
      </View>

      <View style={styles.xrayCard}>
        <Text style={styles.controlCaption}>РЕНТГЕНОВСКИЙ АППАРАТ</Text>
        <Text style={styles.xrayState}>
          {telemetry.xrayState === "READY" ? "Готов" : telemetry.xrayState}
        </Text>
        <Text style={styles.xrayMeta}>
          {telemetry.xrayKv} кВ · {telemetry.xrayMa.toFixed(1)} мА
        </Text>
        <Text style={styles.notice}>
          Запуск экспозиции будет добавлен отдельным этапом с обязательным двухшаговым подтверждением.
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  content: { padding: 18, paddingBottom: 36, gap: 16 },
  hero: {
    backgroundColor: colors.brandDark,
    borderRadius: 24,
    padding: 22,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    gap: 14,
    flexWrap: "wrap"
  },
  eyebrow: { color: "#BFD2FF", fontSize: 12, fontWeight: "800", letterSpacing: 1.4 },
  state: { color: colors.white, fontSize: 34, fontWeight: "900", marginTop: 4 },
  serial: { color: "#DCE7FF", marginTop: 6 },
  connection: {
    backgroundColor: "rgba(255,255,255,0.13)",
    borderWidth: 1,
    borderColor: "rgba(255,255,255,0.22)",
    borderRadius: 16,
    paddingHorizontal: 16,
    paddingVertical: 12
  },
  connectionOff: { backgroundColor: colors.danger },
  connectionTitle: { color: colors.white, fontWeight: "800" },
  connectionMeta: { color: "#DCE7FF", marginTop: 4 },
  grid: { flexDirection: "row", flexWrap: "wrap", gap: 10 },
  batteryRow: { flexDirection: "row", flexWrap: "wrap", gap: 10 },
  sectionTitle: { color: colors.text, fontSize: 18, fontWeight: "900", marginTop: 4 },
  controlPanel: {
    flexDirection: "row",
    gap: 14,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 22,
    padding: 16,
    alignItems: "stretch"
  },
  controlPanelCompact: { flexDirection: "column" },
  manualColumn: { flex: 2 },
  actionColumn: { flex: 1, gap: 10 },
  controlCaption: { color: colors.textMuted, fontSize: 12, fontWeight: "900", letterSpacing: 1.2 },
  manualRow: { flexDirection: "row", gap: 10, marginTop: 10 },
  holdButton: {
    flex: 1,
    minHeight: 126,
    borderRadius: 18,
    backgroundColor: colors.brand,
    alignItems: "center",
    justifyContent: "center"
  },
  holdPressed: { backgroundColor: colors.brandDark, transform: [{ scale: 0.985 }] },
  holdArrow: { color: colors.white, fontSize: 36, fontWeight: "900", lineHeight: 40 },
  holdTitle: { color: colors.white, fontSize: 20, fontWeight: "900" },
  holdHint: { color: "#D9E5FF", fontSize: 11, fontWeight: "800", marginTop: 4 },
  actionButton: {
    minHeight: 54,
    borderRadius: 14,
    backgroundColor: colors.brandSoft,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 12
  },
  actionText: { color: colors.brandDark, fontWeight: "800" },
  secondaryButton: {
    minHeight: 54,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.border,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 12
  },
  secondaryText: { color: colors.text, fontWeight: "700", textAlign: "center" },
  stopButton: {
    minWidth: 145,
    minHeight: 126,
    borderRadius: 20,
    backgroundColor: colors.danger,
    alignItems: "center",
    justifyContent: "center"
  },
  stopLabel: { color: colors.white, fontWeight: "900", fontSize: 32, letterSpacing: 1.5 },
  stopHint: { color: "#FFE1E1", marginTop: 4, fontWeight: "700" },
  disabled: { opacity: 0.35 },
  xrayCard: {
    borderRadius: 22,
    padding: 18,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border
  },
  xrayState: { color: colors.success, fontSize: 28, fontWeight: "900", marginTop: 8 },
  xrayMeta: { color: colors.text, fontSize: 16, fontWeight: "700", marginTop: 4 },
  notice: {
    marginTop: 14,
    padding: 12,
    backgroundColor: colors.brandSoft,
    borderRadius: 12,
    color: colors.brandDark,
    lineHeight: 19
  }
});
