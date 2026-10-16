import { SafeAreaView, StyleSheet, Text, View } from "react-native";
import { StatusBar } from "expo-status-bar";
import { DashboardScreen } from "./src/screens/DashboardScreen";
import { colors } from "./src/theme/colors";
import { useCrawlerController } from "./src/hooks/useCrawlerController";

export default function App() {
  const controller = useCrawlerController();

  return (
    <SafeAreaView style={styles.safe}>
      <StatusBar style="dark" />
      <View style={styles.header}>
        <View style={styles.logoBlock}>
          <Text style={styles.logoText}>АЛИТАС</Text>
        </View>
        <View style={styles.headerText}>
          <Text style={styles.title}>Управление кроулером</Text>
          <Text style={styles.subtitle}>Пульт оператора · прототип 0.1</Text>
        </View>
      </View>

      {controller.telemetry ? (
        <DashboardScreen
          telemetry={controller.telemetry}
          onManualStart={controller.manualStart}
          onManualStop={controller.manualStop}
          onAutoStart={controller.autoStart}
          onStop={controller.stop}
          onToggleStandby={
            controller.telemetry.state === "WAITING"
              ? controller.setStop
              : controller.setStandby
          }
        />
      ) : (
        <View style={styles.loading}>
          <Text style={styles.loadingTitle}>Подключение к контроллеру…</Text>
          <Text style={styles.loadingText}>Инициализация транспорта и телеметрии</Text>
        </View>
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: { flex: 1, backgroundColor: colors.background },
  header: {
    minHeight: 76,
    paddingHorizontal: 18,
    paddingVertical: 10,
    flexDirection: "row",
    alignItems: "center",
    gap: 14,
    backgroundColor: colors.surface,
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  logoBlock: {
    minWidth: 150,
    height: 44,
    justifyContent: "center",
    paddingHorizontal: 4
  },
  logoText: {
    color: colors.brand,
    fontSize: 27,
    fontWeight: "900",
    letterSpacing: 3.5
  },
  headerText: { flex: 1 },
  title: { color: colors.text, fontSize: 18, fontWeight: "900" },
  subtitle: { color: colors.textMuted, fontSize: 12, marginTop: 2 },
  loading: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24 },
  loadingTitle: { color: colors.text, fontSize: 22, fontWeight: "900" },
  loadingText: { color: colors.textMuted, marginTop: 8, textAlign: "center" }
});
