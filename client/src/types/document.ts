import { UUID } from "crypto";

export interface Document {
    id: string,
    userId : UUID
    fileName: string,
    status: string
}
