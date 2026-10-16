export type CrawlerState =
  | "WAITING"
  | "STOP"
  | "MOVING"
  | "EXPOSURE"
  | "CALIBRATION";

export type Direction = "FORWARD" | "BACKWARD";

export type XRayState =
  | "EXPOSURE_FORBIDDEN"
  | "ERROR"
  | "TRAINING_REQUIRED"
  | "READY";

export interface BatteryTelemetry {
  voltage: number;
  current: number;
  temperature: number;
  soc: number;
}

export interface SensorState {
  pipeStart: boolean;
  pipeEnd: boolean;
  water: boolean;
  obstacle: boolean;
}

export interface CrawlerTelemetry {
  serialNumber: string;
  name: string;
  state: CrawlerState;
  direction: Direction | null;
  speedPercent: number;
  distanceMeters: number;
  totalMileageKm: number;
  rollDeg: number;
  pitchDeg: number;
  brakePercent: number;
  magneticSignalPercent: number;
  bleRssiDbm: number;
  driveBattery: BatteryTelemetry;
  xrayBattery: BatteryTelemetry;
  sensors: SensorState;
  xrayState: XRayState;
  xrayKv: number;
  xrayMa: number;
  xrayRemainingSec: number;
  returnReason?: string;
  connected: boolean;
  lastUpdatedAt: number;
}

export type CrawlerCommand =
  | { type: "MANUAL_MOVE_START"; direction: Direction }
  | { type: "MANUAL_MOVE_STOP" }
  | { type: "AUTO_MOVE_START"; direction: Direction }
  | { type: "STOP" }
  | { type: "SET_STATE"; state: "WAITING" | "STOP" };
