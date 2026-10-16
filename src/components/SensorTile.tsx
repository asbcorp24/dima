import { StyleSheet, Text, View } from "react-native";
import { colors } from "../theme/colors";

export function SensorTile({ label, active }: { label: string; active: boolean }) {
  return (
    <View style={[styles.tile, active && styles.activeTile]}>
      <View style={[styles.dot, active && styles.activeDot]} />
      <Text style={[styles.label, active && styles.activeLabel]}>{label}</Text>
      <Text style={[styles.state, active && styles.activeLabel]}>
        {active ? "СРАБОТАЛ" : "Норма"}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  tile: {
    flexGrow: 1,
    flexBasis: 150,
    borderRadius: 16,
    padding: 14,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border
  },
  activeTile: { backgroundColor: colors.dangerSoft, borderColor: colors.danger },
  dot: { width: 10, height: 10, borderRadius: 5, backgroundColor: colors.success },
  activeDot: { backgroundColor: colors.danger },
  label: { marginTop: 10, color: colors.text, fontWeight: "700", fontSize: 14 },
  state: { marginTop: 4, color: colors.textMuted, fontSize: 12 },
  activeLabel: { color: colors.danger }
});
