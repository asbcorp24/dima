import { StyleSheet, Text, View } from "react-native";
import { BatteryTelemetry } from "../types/crawler";
import { colors } from "../theme/colors";

export function BatteryCard({
  title,
  battery
}: {
  title: string;
  battery: BatteryTelemetry;
}) {
  return (
    <View style={styles.card}>
      <View style={styles.topRow}>
        <Text style={styles.title}>{title}</Text>
        <Text style={styles.soc}>{battery.soc}%</Text>
      </View>
      <View style={styles.track}>
        <View style={[styles.fill, { width: `${battery.soc}%` }]} />
      </View>
      <Text style={styles.meta}>
        {battery.voltage.toFixed(1)} В · {battery.current.toFixed(1)} А · {battery.temperature} °C
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    flex: 1,
    minWidth: 260,
    backgroundColor: colors.surface,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: colors.border,
    padding: 16
  },
  topRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  title: { fontSize: 15, fontWeight: "700", color: colors.text },
  soc: { fontSize: 22, fontWeight: "900", color: colors.brand },
  track: {
    height: 10,
    backgroundColor: colors.surfaceMuted,
    borderRadius: 99,
    overflow: "hidden",
    marginTop: 12
  },
  fill: { height: "100%", backgroundColor: colors.brand, borderRadius: 99 },
  meta: { marginTop: 10, color: colors.textMuted, fontSize: 13 }
});
