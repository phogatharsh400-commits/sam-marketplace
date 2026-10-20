-- V1__init_schema.sql
-- Smart Agricultural Marketplace (SAM) Schema Initialization

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    mobile_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION',
    is_mobile_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS farmer_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    village VARCHAR(150) NOT NULL,
    bank_account_number VARCHAR(50),
    bank_ifsc VARCHAR(20),
    preferred_language VARCHAR(20) DEFAULT 'en',
    land_area_acres NUMERIC(8,2) DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS buyer_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(200) NOT NULL,
    gstin VARCHAR(30),
    pan_number VARCHAR(20),
    business_address TEXT NOT NULL,
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    buyer_type VARCHAR(50) DEFAULT 'TRADER',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouse_operator_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    business_name VARCHAR(200) NOT NULL,
    license_number VARCHAR(100),
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouses (
    id UUID PRIMARY KEY,
    operator_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    warehouse_name VARCHAR(200) NOT NULL,
    owner_name VARCHAR(150) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    total_capacity_quintals NUMERIC(12,2) NOT NULL CHECK (total_capacity_quintals > 0),
    available_capacity_quintals NUMERIC(12,2) NOT NULL CHECK (available_capacity_quintals >= 0),
    supported_crops VARCHAR(255) NOT NULL,
    storage_charge_per_quintal_month NUMERIC(10,2) NOT NULL CHECK (storage_charge_per_quintal_month >= 0),
    is_accredited BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS warehouse_bookings (
    id UUID PRIMARY KEY,
    warehouse_id UUID NOT NULL REFERENCES warehouses(id) ON DELETE CASCADE,
    farmer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    crop VARCHAR(50) NOT NULL,
    quantity_quintals NUMERIC(10,2) NOT NULL CHECK (quantity_quintals > 0),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_estimated_cost NUMERIC(12,2) NOT NULL DEFAULT 0.0,
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    remarks TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lots (
    id UUID PRIMARY KEY,
    lot_number VARCHAR(50) NOT NULL UNIQUE,
    farmer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    crop VARCHAR(50) NOT NULL,
    variety VARCHAR(100) NOT NULL,
    quantity_quintals NUMERIC(10,2) NOT NULL CHECK (quantity_quintals > 0),
    unit VARCHAR(20) NOT NULL DEFAULT 'QUINTAL',
    grade VARCHAR(20) NOT NULL DEFAULT 'GRADE_A',
    moisture_percentage NUMERIC(5,2) NOT NULL CHECK (moisture_percentage >= 0 AND moisture_percentage <= 100),
    state VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    village VARCHAR(150) NOT NULL,
    pickup_location TEXT NOT NULL,
    min_price_per_quintal NUMERIC(10,2) NOT NULL CHECK (min_price_per_quintal > 0),
    expected_dispatch_date DATE NOT NULL,
    auction_closing_date DATE NOT NULL,
    description TEXT,
    storage_requirement VARCHAR(100) DEFAULT 'DRY_COVERED',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lot_images (
    id UUID PRIMARY KEY,
    lot_id UUID NOT NULL REFERENCES lots(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bids (
    id UUID PRIMARY KEY,
    lot_id UUID NOT NULL REFERENCES lots(id) ON DELETE CASCADE,
    buyer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    bid_price_per_quintal NUMERIC(10,2) NOT NULL CHECK (bid_price_per_quintal > 0),
    quantity_quintals NUMERIC(10,2) NOT NULL CHECK (quantity_quintals > 0),
    total_amount NUMERIC(14,2) NOT NULL CHECK (total_amount > 0),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sale_contracts (
    id UUID PRIMARY KEY,
    contract_number VARCHAR(50) NOT NULL UNIQUE,
    lot_id UUID NOT NULL REFERENCES lots(id),
    farmer_id UUID NOT NULL REFERENCES users(id),
    buyer_id UUID NOT NULL REFERENCES users(id),
    accepted_bid_id UUID NOT NULL REFERENCES bids(id),
    quantity_quintals NUMERIC(10,2) NOT NULL CHECK (quantity_quintals > 0),
    price_per_quintal NUMERIC(10,2) NOT NULL CHECK (price_per_quintal > 0),
    total_amount NUMERIC(14,2) NOT NULL CHECK (total_amount > 0),
    delivery_terms TEXT NOT NULL,
    dispatch_deadline DATE NOT NULL,
    delivery_deadline DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AWAITING_ESCROW',
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    contract_id UUID NOT NULL REFERENCES sale_contracts(id),
    payer_id UUID NOT NULL REFERENCES users(id),
    payee_id UUID NOT NULL REFERENCES users(id),
    amount NUMERIC(14,2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(30) NOT NULL DEFAULT 'UPI',
    status VARCHAR(30) NOT NULL DEFAULT 'INITIATED',
    transaction_reference VARCHAR(100) NOT NULL UNIQUE,
    idempotency_key VARCHAR(100) UNIQUE,
    simulation_mode BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS deliveries (
    id UUID PRIMARY KEY,
    contract_id UUID NOT NULL UNIQUE REFERENCES sale_contracts(id),
    quantity_delivered_quintals NUMERIC(10,2) NOT NULL CHECK (quantity_delivered_quintals >= 0),
    delivery_date DATE NOT NULL,
    quality_remarks TEXT,
    buyer_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    farmer_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    weight_slip_url TEXT,
    delivery_photos_metadata TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS disputes (
    id UUID PRIMARY KEY,
    contract_id UUID NOT NULL REFERENCES sale_contracts(id),
    raised_by_user_id UUID NOT NULL REFERENCES users(id),
    reason VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    evidence_file_url TEXT,
    requested_resolution VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    resolution_notes TEXT,
    resolved_at TIMESTAMP WITH TIME ZONE,
    resolved_by_admin_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS dispute_evidence (
    id UUID PRIMARY KEY,
    dispute_id UUID NOT NULL REFERENCES disputes(id) ON DELETE CASCADE,
    file_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    uploaded_by_user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS price_benchmarks (
    id UUID PRIMARY KEY,
    crop VARCHAR(50) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL DEFAULT 'Haryana',
    current_mandi_price NUMERIC(10,2) NOT NULL,
    min_price NUMERIC(10,2) NOT NULL,
    max_price NUMERIC(10,2) NOT NULL,
    modal_price NUMERIC(10,2) NOT NULL,
    msp NUMERIC(10,2) NOT NULL,
    data_source VARCHAR(100) NOT NULL DEFAULT 'Agmarknet Simulated Mandi API',
    price_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    reference_id VARCHAR(100),
    reference_type VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indices for performance
CREATE INDEX IF NOT EXISTS idx_lots_crop ON lots(crop);
CREATE INDEX IF NOT EXISTS idx_lots_district ON lots(district);
CREATE INDEX IF NOT EXISTS idx_lots_status ON lots(status);
CREATE INDEX IF NOT EXISTS idx_bids_lot_id ON bids(lot_id);
CREATE INDEX IF NOT EXISTS idx_bids_buyer_id ON bids(buyer_id);
CREATE INDEX IF NOT EXISTS idx_contracts_farmer_id ON sale_contracts(farmer_id);
CREATE INDEX IF NOT EXISTS idx_contracts_buyer_id ON sale_contracts(buyer_id);
CREATE INDEX IF NOT EXISTS idx_payments_contract_id ON payments(contract_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_benchmarks_crop_district ON price_benchmarks(crop, district, price_date);
