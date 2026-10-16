import { StyleSheet, Text, View } from "react-native";
import { colors } from "../theme/colors";

interface Props {
  label: string;
  value: string;
  detail?: string;
}

export function MetricCard({ label, value, detail }: Props) {
  return (
    <View style={styles.card}>
      <Text style={styles.label}>{label}</Text>
      <Text style={styles.value}>{value}</Text>
      {detail ? <Text style={styles.detail}>{detail}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    minWidth: 132,
    flexGrow: 1,
    flexBasis: 145,
    backgroundColor: colors.surface,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 18,
    padding: 16
  },
  label: { color: colors.textMuted, fontSize: 13, fontWeight: "600" },
  value: { color: colors.text, fontSize: 24, fontWeight: "800", marginTop: 6 },
  detail: { color: colors.textMuted, fontSize: 12, marginTop: 4 }
});
